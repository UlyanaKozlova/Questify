package com.example.questify.domain.usecase.game.clothes;


import com.example.questify.R;
import com.example.questify.data.repository.ClothingRepository;
import com.example.questify.domain.model.Clothing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

public class SeedClothingUseCase {

    private static final String DEFAULT_NAME = "default";

    private final ClothingRepository clothingRepository;

    @Inject
    public SeedClothingUseCase(ClothingRepository clothingRepository) {
        this.clothingRepository = clothingRepository;
    }

    private static final List<SeedItem> SEED_ITEMS = Arrays.asList(
            new SeedItem("green_suit", "Зелёный костюм", 30, R.drawable.cat_in_green_suit),
            new SeedItem("sport_suit", "Спортивный костюм", 40, R.drawable.cat_in_sport_suit),
            new SeedItem("brown_coat", "Коричневое пальто", 60, R.drawable.cat_with_brown_coat),
            new SeedItem("corset_dress", "Платье с корсетом", 50, R.drawable.cat_with_corset_dress),
            new SeedItem("denim_overalls", "Джинсовый комбинезон", 30, R.drawable.cat_with_denim_overalls),
            new SeedItem("evening_dress", "Вечернее платье", 70, R.drawable.cat_with_evening_dress),
            new SeedItem("jeans_tshirt", "Джинсы и футболка", 25, R.drawable.cat_with_jeans_and_tshirt),
            new SeedItem("leather_pants_blue_sweater", "Кожаные штаны и свитер", 30, R.drawable.cat_with_leather_pants_blue_sweater),
            new SeedItem("pants_shirt", "Брюки и рубашка", 35, R.drawable.cat_with_pants_and_shirt),
            new SeedItem("pink_dress", "Розовое платье", 90, R.drawable.cat_with_pink_dress),
            new SeedItem("shorts_tanktop", "Шорты и майка", 45, R.drawable.cat_with_shorts_and_tanktop),
            new SeedItem("white_tshirt_jeans", "Белая футболка и джинсы", 40, R.drawable.cat_with_white_t_shirt_and_jeans)
    );

    public void execute() {
        List<Clothing> existing = clothingRepository.getAll();

        Map<String, Clothing> byGlobalId = new HashMap<>();
        Map<String, Clothing> byName = new HashMap<>();
        for (Clothing c : existing) {
            if (c.getGlobalId() != null) byGlobalId.put(c.getGlobalId(), c);
            if (c.getName() != null) byName.put(c.getName(), c);
        }

        Set<String> validGlobalIds = new HashSet<>();
        validGlobalIds.add(stableId(DEFAULT_NAME));

        for (SeedItem item : SEED_ITEMS) {
            String stableId = stableId(item.key);
            validGlobalIds.add(stableId);

            Clothing fromId = byGlobalId.get(stableId);
            Clothing fromLegacyName = byName.get(item.displayName);
            Clothing fromLegacyKey = byName.get(item.key);

            Clothing match = fromId != null ? fromId
                    : fromLegacyName != null ? fromLegacyName
                    : fromLegacyKey;

            if (match == null) {
                Clothing toInsert = new Clothing(stableId, item.displayName, item.price, item.imageResId, System.currentTimeMillis());
                clothingRepository.save(toInsert);
            } else {
                boolean changed = !item.displayName.equals(match.getName())
                        || match.getPrice() != item.price
                        || match.getImageResId() != item.imageResId
                        || !stableId.equals(match.getGlobalId());

                if (changed) {
                    Clothing updated = new Clothing(
                            stableId.equals(match.getGlobalId()) ? match.getGlobalId() : stableId,
                            item.displayName,
                            item.price,
                            item.imageResId,
                            System.currentTimeMillis()
                    );
                    if (stableId.equals(match.getGlobalId())) {
                        clothingRepository.update(updated);
                    } else {
                        clothingRepository.delete(match);
                        clothingRepository.save(updated);
                    }
                }
            }
        }

        for (Clothing c : existing) {
            if (DEFAULT_NAME.equals(c.getName())) {
                continue;
            }
            if (!validGlobalIds.contains(c.getGlobalId())) {
                clothingRepository.delete(c);
            }
        }
    }

    private static String stableId(String key) {
        return "clothing_" + key;
    }

    private static class SeedItem {
        final String key;
        final String displayName;
        final int price;
        final int imageResId;

        SeedItem(String key, String displayName, int price, int imageResId) {
            this.key = key;
            this.displayName = displayName;
            this.price = price;
            this.imageResId = imageResId;
        }
    }
}
