package com.example.questify.data.di;

import android.content.Context;

import com.example.questify.sync.AuthenticationManager;
import com.example.questify.sync.SyncManager;
import com.example.questify.UserSession;
import com.example.questify.data.repository.*;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AuthenticationModule {

    @Provides
    @Singleton
    public AuthenticationManager provideAuthenticationManager(
            UserSession userSession,
            @ApplicationContext Context context) {
        return new AuthenticationManager(userSession, context);
    }

    @Provides
    @Singleton
    public SyncManager provideSyncManager(
            @ApplicationContext Context context,
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            SubtaskRepository subtaskRepository,
            UserRepository userRepository,
            PetRepository petRepository,
            ClothingRepository clothingRepository,
            PetClothingRefRepository petClothingRefRepository) {
        return new SyncManager(
                context,
                taskRepository,
                projectRepository,
                subtaskRepository,
                userRepository,
                petRepository,
                clothingRepository,
                petClothingRefRepository
        );
    }
}