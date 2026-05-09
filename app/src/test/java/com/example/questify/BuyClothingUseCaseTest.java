package com.example.questify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.questify.data.repository.PetClothingRefRepository;
import com.example.questify.data.repository.PetRepository;
import com.example.questify.data.repository.UserRepository;
import com.example.questify.domain.model.Clothing;
import com.example.questify.domain.model.Pet;
import com.example.questify.domain.model.PetClothingRef;
import com.example.questify.domain.model.User;
import com.example.questify.domain.usecase.game.clothes.BuyClothingUseCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class BuyClothingUseCaseTest {

    private UserRepository userRepository;
    private PetClothingRefRepository petClothingRefRepository;
    private BuyClothingUseCase useCase;

    private User user;

    @Before
    public void setUp() {
        userRepository = mock(UserRepository.class);
        PetRepository petRepository = mock(PetRepository.class);
        petClothingRefRepository = mock(PetClothingRefRepository.class);

        user = new User("user-1", "name", "", 0, 100, 100, 0L);
        Pet pet = new Pet("pet-1", "user-1", "clothing_default", 0L);

        when(userRepository.getUser()).thenReturn(user);
        when(petRepository.getPet()).thenReturn(pet);

        useCase = new BuyClothingUseCase(userRepository, petRepository, petClothingRefRepository);
    }

    private static Clothing clothing(int price) {
        return new Clothing("clothing-x", "X", price, 0, 0L);
    }

    @Test
    public void buy_coinsGreaterThanPrice_allowed() {
        user.setCoins(100);
        boolean ok = useCase.execute(clothing(30));
        assertTrue(ok);
        assertEquals(70, user.getCoins());
        verify(userRepository).update(user);
        verify(petClothingRefRepository).save(any(PetClothingRef.class));
    }

    @Test
    public void buy_coinsLessThanPrice_rejected() {
        user.setCoins(10);
        boolean ok = useCase.execute(clothing(30));
        assertFalse(ok);
        assertEquals(10, user.getCoins());
        verify(userRepository, never()).update(any(User.class));
        verify(petClothingRefRepository, never()).save(any(PetClothingRef.class));
    }

    @Test
    public void buy_coinsEqualPrice_allowed() {
        user.setCoins(50);
        boolean ok = useCase.execute(clothing(50));
        assertTrue(ok);
        assertEquals(0, user.getCoins());
    }

    @Test
    public void buy_decrementsBalanceAndSavesRef() {
        user.setCoins(200);
        Clothing c = clothing(75);

        useCase.execute(c);

        assertEquals(125, user.getCoins());
        ArgumentCaptor<PetClothingRef> captor = ArgumentCaptor.forClass(PetClothingRef.class);
        verify(petClothingRefRepository, times(1)).save(captor.capture());
        PetClothingRef saved = captor.getValue();
        assertEquals("pet-1", saved.getPetGlobalId());
        assertEquals("clothing-x", saved.getClothingGlobalId());
    }
}
