package com.example.questify.sync;

import android.content.Context;
import android.util.Log;

import com.example.questify.data.repository.*;
import com.example.questify.data.remote.model.*;
import com.example.questify.domain.model.*;
import com.example.questify.domain.model.enums.Difficulty;
import com.example.questify.domain.model.enums.Priority;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SyncManager {
    private static final String TAG = "SyncManager";

    private static final String TASKS_COLLECTION = "tasks";
    private static final String PROJECTS_COLLECTION = "projects";
    private static final String SUBTASKS_COLLECTION = "subtasks";
    private static final String USERS_COLLECTION = "users";
    private static final String PETS_COLLECTION = "pets";
    private static final String CLOTHING_COLLECTION = "clothing";
    private static final String PET_CLOTHING_REF_COLLECTION = "pet_clothing_ref";

    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ScheduledExecutorService debounceExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> pendingSync;

    private final Context context;

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final SubtaskRepository subtaskRepository;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final ClothingRepository clothingRepository;
    private final PetClothingRefRepository petClothingRefRepository;

    private boolean firebaseInitialized = false;
    private final AtomicBoolean isSyncing = new AtomicBoolean(false);

    @Inject
    public SyncManager(Context context,
                       TaskRepository taskRepository,
                       ProjectRepository projectRepository,
                       SubtaskRepository subtaskRepository,
                       UserRepository userRepository,
                       PetRepository petRepository,
                       ClothingRepository clothingRepository,
                       PetClothingRefRepository petClothingRefRepository) {
        this.context = context;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.subtaskRepository = subtaskRepository;
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.clothingRepository = clothingRepository;
        this.petClothingRefRepository = petClothingRefRepository;
        initFirebase();
    }

    private void initFirebase() {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context);
            }
            this.firestore = FirebaseFirestore.getInstance();
            this.auth = FirebaseAuth.getInstance();
            firebaseInitialized = true;
        } catch (Exception e) {
            firebaseInitialized = false;
        }
    }

    public synchronized void scheduleSyncSoon() {
        if (pendingSync != null && !pendingSync.isDone()) {
            pendingSync.cancel(false);
        }
        pendingSync = debounceExecutor.schedule(() -> syncAllToCloud(null), 3, TimeUnit.SECONDS);
    }

    private boolean isNotFirebaseReady() {
        return !firebaseInitialized || firestore == null || auth == null;
    }

    public void syncAllFromCloud(Runnable onComplete) {
        if (isNotFirebaseReady()) {
            Log.w(TAG, "Firebase not ready, skipping sync");
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        if (auth.getCurrentUser() == null) {
            Log.w(TAG, "No authenticated user, skipping sync");
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        if (!isSyncing.compareAndSet(false, true)) {
            Log.w(TAG, "Sync already in progress");
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }
        String userId = auth.getCurrentUser().getUid();

        AtomicInteger pendingTasks = new AtomicInteger(7);
        Runnable onTaskComplete = () -> {
            if (pendingTasks.decrementAndGet() == 0) {
                isSyncing.set(false);
                Log.d(TAG, "All sync from cloud completed");
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        };

        syncTasksFromCloud(userId, onTaskComplete);
        syncProjectsFromCloud(userId, onTaskComplete);
        syncSubtasksFromCloud(userId, onTaskComplete);
        syncUserFromCloud(userId, onTaskComplete);
        syncPetFromCloud(userId, onTaskComplete);
        syncClothingFromCloud(onTaskComplete);
        syncPetClothingRefFromCloud(onTaskComplete);
    }

    public void syncAllToCloud(Runnable onComplete) {
        if (isNotFirebaseReady()) {
            Log.w(TAG, "Firebase not ready, skipping upload");
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        if (auth.getCurrentUser() == null) {
            Log.w(TAG, "No authenticated user, skipping upload");
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        executor.execute(() -> {
            try {
                WriteBatch batch = firestore.batch();

                List<String> taskIds = addTasksToBatch(batch);
                List<String> projectIds = addProjectsToBatch(batch);
                List<String> subtaskIds = addSubtasksToBatch(batch);
                boolean userNeedsSync = addUserToBatch(batch);
                boolean petNeedsSync = addPetToBatch(batch);
                List<String> clothingIds = addClothingToBatch(batch);
                addPetClothingRefToBatch(batch);

                int totalChanges = taskIds.size() + projectIds.size() + subtaskIds.size()
                        + clothingIds.size()
                        + (userNeedsSync ? 1 : 0) + (petNeedsSync ? 1 : 0);

                if (totalChanges > 0) {
                    Log.d(TAG, "Uploading " + totalChanges + " changes to cloud");
                    batch.commit().addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "All data synced to cloud successfully");
                        executor.execute(() -> {
                            for (String id : taskIds) {
                                taskRepository.clearSyncFlag(id);
                            }
                            for (String id : projectIds) {
                                projectRepository.clearSyncFlag(id);
                            }
                            for (String id : subtaskIds) {
                                subtaskRepository.clearSyncFlag(id);
                            }
                            for (String id : clothingIds) {
                                clothingRepository.clearSyncFlag(id);
                            }
                            if (userNeedsSync) {
                                userRepository.clearSyncFlag();
                            }
                            if (petNeedsSync) {
                                petRepository.clearSyncFlag();
                            }
                        });
                        if (onComplete != null) onComplete.run();
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Failed to sync to cloud", e);
                        if (onComplete != null) onComplete.run();
                    });
                } else {
                    Log.d(TAG, "No data to sync");
                    if (onComplete != null) onComplete.run();
                }
            } catch (Exception e) {
                Log.e(TAG, "Sync to cloud failed", e);
                if (onComplete != null) onComplete.run();
            }
        });
    }

    private void syncTasksFromCloud(String userId, Runnable onComplete) {
        if (firestore == null) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        firestore.collection(TASKS_COLLECTION)
                .whereEqualTo("userGlobalId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> executor.execute(() -> {
                    Log.d(TAG, "Syncing " + querySnapshot.size() + " tasks from cloud");
                    for (var doc : querySnapshot.getDocuments()) {
                        TaskRemote remote = doc.toObject(TaskRemote.class);
                        if (remote == null) {
                            continue;
                        }
                        if (remote.isDeleted) {
                            Task local = taskRepository.getByGlobalId(remote.globalId);
                            long remoteUpdatedAt = remote.updatedAt != null ? remote.updatedAt.toDate().getTime() : 0;
                            if (local == null || local.getUpdatedAt() <= remoteUpdatedAt) {
                                taskRepository.deleteByGlobalId(remote.globalId);
                                Log.d(TAG, "Task deleted locally: " + remote.globalId);
                            } else {
                                Log.d(TAG, "Skip cloud deletion — local task is newer: " + remote.globalId);
                            }
                        } else {
                            Task task = convertToTask(remote);
                            taskRepository.saveOrUpdateFromSync(task);
                            Log.d(TAG, "Task synced: " + remote.taskName);
                        }
                    }
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync tasks: " + e.getMessage());
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }

    private void syncProjectsFromCloud(String userId, Runnable onComplete) {
        if (firestore == null) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        firestore.collection(PROJECTS_COLLECTION)
                .whereEqualTo("userGlobalId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> executor.execute(() -> {
                    Log.d(TAG, "Syncing " + querySnapshot.size() + " projects from cloud");
                    for (var doc : querySnapshot.getDocuments()) {
                        ProjectRemote remote = doc.toObject(ProjectRemote.class);
                        if (remote == null) {
                            continue;
                        }
                        if (remote.isDeleted) {
                            Project localProject = projectRepository.getByGlobalId(remote.globalId);
                            long remoteUpdatedAt = remote.updatedAt != null ? remote.updatedAt.toDate().getTime() : 0;
                            if (localProject == null || localProject.getUpdatedAt() <= remoteUpdatedAt) {
                                projectRepository.deleteByGlobalId(remote.globalId);
                                Log.d(TAG, "Project deleted locally: " + remote.globalId);
                            } else {
                                Log.d(TAG, "Skip cloud deletion — local project is newer: " + remote.globalId);
                            }
                        } else {
                            Project project = convertToProject(remote);
                            projectRepository.saveOrUpdateFromSync(project);
                            Log.d(TAG, "Project synced: " + remote.projectName);
                        }
                    }
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync projects: " + e.getMessage());
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }

    private void syncSubtasksFromCloud(String userId, Runnable onComplete) {
        if (firestore == null) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        firestore.collection(SUBTASKS_COLLECTION)
                .whereEqualTo("userGlobalId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> executor.execute(() -> {
                    Log.d(TAG, "Syncing " + querySnapshot.size() + " subtasks from cloud");
                    for (var doc : querySnapshot.getDocuments()) {
                        SubtaskRemote remote = doc.toObject(SubtaskRemote.class);
                        if (remote == null) {
                            continue;
                        }
                        if (remote.isDeleted) {
                            Subtask localSubtask = subtaskRepository.getByGlobalId(remote.globalId);
                            long remoteUpdatedAt = remote.updatedAt != null ? remote.updatedAt.toDate().getTime() : 0;
                            if (localSubtask == null || localSubtask.getUpdatedAt() <= remoteUpdatedAt) {
                                subtaskRepository.deleteByGlobalId(remote.globalId);
                                Log.d(TAG, "Subtask deleted locally: " + remote.globalId);
                            } else {
                                Log.d(TAG, "Skip cloud deletion — local subtask is newer: " + remote.globalId);
                            }
                        } else {
                            Subtask subtask = convertToSubtask(remote);
                            subtaskRepository.saveOrUpdateFromSync(subtask);
                            Log.d(TAG, "Subtask synced: " + remote.subtaskName);
                        }
                    }
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync subtasks: " + e.getMessage());
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }

    private void syncUserFromCloud(String userId, Runnable onComplete) {
        if (firestore == null) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserRemote remote = documentSnapshot.toObject(UserRemote.class);
                        if (remote != null && !remote.isDeleted) {
                            User user = convertToUser(remote);
                            userRepository.saveOrUpdateFromSync(user);
                            Log.d(TAG, "User synced");
                        }
                    }
                    if (onComplete != null) {
                        onComplete.run();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync user: " + e.getMessage());
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }

    private void syncPetFromCloud(String userId, Runnable onComplete) {
        if (firestore == null) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        firestore.collection(PETS_COLLECTION)
                .whereEqualTo("userGlobalId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> executor.execute(() -> {
                    Log.d(TAG, "Syncing " + querySnapshot.size() + " pets from cloud");
                    for (var doc : querySnapshot.getDocuments()) {
                        PetRemote remote = doc.toObject(PetRemote.class);
                        if (remote != null && !remote.isDeleted) {
                            Pet pet = convertToPet(remote);
                            petRepository.saveOrUpdateFromSync(pet);
                            Log.d(TAG, "Pet synced");
                        }
                    }
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync pet: " + e.getMessage());
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }

    private void syncClothingFromCloud(Runnable onComplete) {
        if (firestore == null) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        firestore.collection(CLOTHING_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> executor.execute(() -> {
                    Log.d(TAG, "Syncing " + querySnapshot.size() + " clothing items from cloud");
                    for (var doc : querySnapshot.getDocuments()) {
                        ClothingRemote remote = doc.toObject(ClothingRemote.class);
                        if (remote != null && !remote.isDeleted) {
                            Clothing clothing = convertToClothing(remote);
                            clothingRepository.saveOrUpdateFromSync(clothing);
                            Log.d(TAG, "Clothing synced: " + remote.name);
                        }
                    }
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync clothing: " + e.getMessage());
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }

    private void syncPetClothingRefFromCloud(Runnable onComplete) {
        if (firestore == null) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        firestore.collection(PET_CLOTHING_REF_COLLECTION)
                .get()
                .addOnSuccessListener(querySnapshot -> executor.execute(() -> {
                    Log.d(TAG, "Syncing " + querySnapshot.size() + " pet clothing refs from cloud");
                    for (var doc : querySnapshot.getDocuments()) {
                        PetClothingRefRemote remote = doc.toObject(PetClothingRefRemote.class);
                        if (remote != null) {
                            PetClothingRef ref = new PetClothingRef(remote.petGlobalId, remote.clothingGlobalId);
                            petClothingRefRepository.saveFromSync(ref);
                            Log.d(TAG, "Pet clothing ref synced");
                        }
                    }
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }))
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to sync pet clothing ref: " + e.getMessage());
                    if (onComplete != null) {
                        onComplete.run();
                    }
                });
    }

    private List<String> addTasksToBatch(WriteBatch batch) {
        List<Task> tasks = taskRepository.getNeedingSync();
        List<Task> deletedTasks = taskRepository.getDeletedNeedingSync();
        List<String> syncedIds = new ArrayList<>();
        Log.d(TAG, "Tasks needing sync: " + tasks.size() + ", deleted: " + deletedTasks.size());

        for (Task task : tasks) {
            TaskRemote remote = new TaskRemote();
            remote.globalId = task.getGlobalId();
            remote.projectGlobalId = task.getProjectGlobalId();
            remote.userGlobalId = task.getUserGlobalId();
            remote.isDone = task.isDone();
            remote.taskName = task.getTaskName();
            remote.description = task.getDescription();
            remote.priority = task.getPriority() != null ? task.getPriority().name() : null;
            remote.difficulty = task.getDifficulty() != null ? task.getDifficulty().name() : null;
            remote.deadline = new Timestamp(task.getDeadline() / 1000, 0);
            remote.updatedAt = new Timestamp(task.getUpdatedAt() / 1000, 0);
            remote.isDeleted = false;

            batch.set(firestore.collection(TASKS_COLLECTION)
                    .document(task.getGlobalId()), remote.toMap(), SetOptions.merge());
            syncedIds.add(task.getGlobalId());
            Log.d(TAG, "Added task to batch: " + task.getTaskName());
        }

        for (Task task : deletedTasks) {
            batch.set(firestore.collection(TASKS_COLLECTION)
                    .document(task.getGlobalId()), buildTombstone(), SetOptions.merge());
            syncedIds.add(task.getGlobalId());
            Log.d(TAG, "Added deleted task to batch: " + task.getGlobalId());
        }

        return syncedIds;
    }

    private List<String> addProjectsToBatch(WriteBatch batch) {
        List<Project> projects = projectRepository.getNeedingSync();
        List<Project> deletedProjects = projectRepository.getDeletedNeedingSync();
        List<String> syncedIds = new ArrayList<>();
        Log.d(TAG, "Projects needing sync: " + projects.size() + ", deleted: " + deletedProjects.size());

        for (Project project : projects) {
            ProjectRemote remote = new ProjectRemote();
            remote.globalId = project.getGlobalId();
            remote.userGlobalId = project.getUserGlobalId();
            remote.projectName = project.getProjectName();
            remote.color = project.getColor();
            remote.updatedAt = new Timestamp(project.getUpdatedAt() / 1000, 0);
            remote.isDeleted = false;

            batch.set(firestore.collection(PROJECTS_COLLECTION)
                    .document(project.getGlobalId()), remote.toMap(), SetOptions.merge());
            syncedIds.add(project.getGlobalId());
            Log.d(TAG, "Added project to batch: " + project.getProjectName());
        }

        for (Project project : deletedProjects) {
            batch.set(firestore.collection(PROJECTS_COLLECTION)
                    .document(project.getGlobalId()), buildTombstone(), SetOptions.merge());
            syncedIds.add(project.getGlobalId());
            Log.d(TAG, "Added deleted project to batch: " + project.getGlobalId());
        }

        return syncedIds;
    }

    private List<String> addSubtasksToBatch(WriteBatch batch) {
        List<Subtask> subtasks = subtaskRepository.getNeedingSync();
        List<Subtask> deletedSubtasks = subtaskRepository.getDeletedNeedingSync();
        List<String> syncedIds = new ArrayList<>();
        Log.d(TAG, "Subtasks needing sync: " + subtasks.size() + ", deleted: " + deletedSubtasks.size());

        for (Subtask subtask : subtasks) {
            SubtaskRemote remote = new SubtaskRemote();
            remote.globalId = subtask.getGlobalId();
            remote.taskGlobalId = subtask.getTaskGlobalId();
            remote.userGlobalId = subtask.getUserGlobalId();
            remote.isDone = subtask.isDone();
            remote.subtaskName = subtask.getSubtaskName();
            remote.updatedAt = new Timestamp(subtask.getUpdatedAt() / 1000, 0);
            remote.isDeleted = false;

            batch.set(firestore.collection(SUBTASKS_COLLECTION)
                    .document(subtask.getGlobalId()), remote.toMap(), SetOptions.merge());
            syncedIds.add(subtask.getGlobalId());
            Log.d(TAG, "Added subtask to batch: " + subtask.getSubtaskName());
        }

        for (Subtask subtask : deletedSubtasks) {
            batch.set(firestore.collection(SUBTASKS_COLLECTION)
                    .document(subtask.getGlobalId()), buildTombstone(), SetOptions.merge());
            syncedIds.add(subtask.getGlobalId());
            Log.d(TAG, "Added deleted subtask to batch: " + subtask.getGlobalId());
        }

        return syncedIds;
    }

    private boolean addUserToBatch(WriteBatch batch) {
        User user = userRepository.getNeedingSync();
        if (user != null) {
            UserRemote remote = new UserRemote();
            remote.globalId = user.getGlobalId();
            remote.username = user.getUsername();
            remote.level = user.getLevel();
            remote.coins = user.getCoins();
            remote.updatedAt = new Timestamp(user.getUpdatedAt() / 1000, 0);
            remote.isDeleted = false;

            batch.set(firestore.collection(USERS_COLLECTION)
                    .document(user.getGlobalId()), remote.toMap(), SetOptions.merge());

            Log.d(TAG, "Added user to batch");
            return true;
        }
        return false;
    }

    private boolean addPetToBatch(WriteBatch batch) {
        Pet pet = petRepository.getNeedingSync();
        if (pet != null) {
            PetRemote remote = new PetRemote();
            remote.globalId = pet.getGlobalId();
            remote.userGlobalId = pet.getUserGlobalId();
            remote.currentClothingGlobalId = pet.getCurrentClothingGlobalId();
            remote.updatedAt = new Timestamp(pet.getUpdatedAt() / 1000, 0);
            remote.isDeleted = false;

            batch.set(firestore.collection(PETS_COLLECTION)
                    .document(pet.getGlobalId()), remote.toMap(), SetOptions.merge());

            Log.d(TAG, "Added pet to batch");
            return true;
        }
        return false;
    }

    private List<String> addClothingToBatch(WriteBatch batch) {
        List<Clothing> clothingList = clothingRepository.getNeedingSync();
        List<String> syncedIds = new ArrayList<>();
        Log.d(TAG, "Clothing needing sync: " + clothingList.size());
        for (Clothing clothing : clothingList) {
            ClothingRemote remote = new ClothingRemote();
            remote.globalId = clothing.getGlobalId();
            remote.name = clothing.getName();
            remote.price = clothing.getPrice();
            remote.imageResId = clothing.getImageResId();
            remote.updatedAt = new Timestamp(clothing.getUpdatedAt() / 1000, 0);
            remote.isDeleted = false;

            batch.set(firestore.collection(CLOTHING_COLLECTION)
                    .document(clothing.getGlobalId()), remote.toMap(), SetOptions.merge());

            syncedIds.add(clothing.getGlobalId());
            Log.d(TAG, "Added clothing to batch: " + clothing.getName());
        }
        return syncedIds;
    }

    private void addPetClothingRefToBatch(WriteBatch batch) {
        List<PetClothingRef> refs = petClothingRefRepository.getPetClothingRefForSync();
        Log.d(TAG, "Pet clothing refs total: " + refs.size());
        for (PetClothingRef ref : refs) {
            PetClothingRefRemote remote = new PetClothingRefRemote();
            remote.petGlobalId = ref.getPetGlobalId();
            remote.clothingGlobalId = ref.getClothingGlobalId();

            String docId = ref.getPetGlobalId() + "_" + ref.getClothingGlobalId();
            batch.set(firestore.collection(PET_CLOTHING_REF_COLLECTION)
                    .document(docId), remote.toMap(), SetOptions.merge());
            Log.d(TAG, "Added pet clothing ref to batch: " + docId);
        }
    }

    private Task convertToTask(TaskRemote remote) {
        Task task = new Task();
        task.setGlobalId(remote.globalId);
        task.setProjectGlobalId(remote.projectGlobalId);
        task.setUserGlobalId(remote.userGlobalId);
        task.setDone(remote.isDone);
        task.setTaskNameWithoutValidation(remote.taskName != null ? remote.taskName : "");
        task.setDescription(remote.description);
        task.setDeadlineWithoutValidation(remote.deadline != null
                ? remote.deadline.toDate().getTime()
                : 0);
        task.setUpdatedAt(remote.updatedAt != null
                ? remote.updatedAt.toDate().getTime()
                : 0);

        try {
            if (remote.priority != null) {
                task.setPriority(Priority.valueOf(remote.priority));
            }
        } catch (IllegalArgumentException e) {
            task.setPriority(Priority.MEDIUM);
        }

        try {
            if (remote.difficulty != null) {
                task.setDifficulty(Difficulty.valueOf(remote.difficulty));
            }
        } catch (IllegalArgumentException e) {
            task.setDifficulty(Difficulty.MEDIUM);
        }
        return task;
    }

    private Project convertToProject(ProjectRemote remote) {
        return new Project(
                remote.globalId,
                remote.userGlobalId,
                remote.projectName,
                remote.color,
                remote.updatedAt != null
                        ? remote.updatedAt.toDate().getTime()
                        : 0
        );
    }

    private Subtask convertToSubtask(SubtaskRemote remote) {
        Subtask subtask = new Subtask(
                remote.globalId,
                remote.taskGlobalId,
                remote.isDone,
                remote.subtaskName,
                remote.updatedAt != null
                        ? remote.updatedAt.toDate().getTime()
                        : 0
        );
        subtask.setUserGlobalId(remote.userGlobalId);
        return subtask;
    }

    private User convertToUser(UserRemote remote) {
        return new User(
                remote.globalId,
                remote.username,
                "",
                remote.level,
                remote.coins,
                remote.coins,
                remote.updatedAt != null
                        ? remote.updatedAt.toDate().getTime()
                        : 0
        );
    }

    private Pet convertToPet(PetRemote remote) {
        return new Pet(
                remote.globalId,
                remote.userGlobalId,
                remote.currentClothingGlobalId,
                remote.updatedAt != null
                        ? remote.updatedAt.toDate().getTime()
                        : 0
        );
    }

    private Clothing convertToClothing(ClothingRemote remote) {
        return new Clothing(
                remote.globalId,
                remote.name,
                remote.price,
                remote.imageResId,
                remote.updatedAt != null
                        ? remote.updatedAt.toDate().getTime()
                        : 0
        );
    }

    private Map<String, Object> buildTombstone() {
        Map<String, Object> tombstone = new HashMap<>();
        tombstone.put("isDeleted", true);
        tombstone.put("updatedAt", new Timestamp(System.currentTimeMillis() / 1000, 0));
        return tombstone;
    }
}