package org.patinanetwork.codebloom.shared.tag;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;
import org.patinanetwork.codebloom.common.utils.pair.Pair;

public class ParentTags {
    public static final Map<Tag, List<Tag>> ENUM_TO_ENUM_LIST = generate();

    // help with type inference
    private static Pair<Tag, List<Tag>> pairOf(Tag left, List<Tag> right) {
        return Pair.of(left, right);
    }

    private static Map<Tag, List<Tag>> generate() {
        return Arrays.stream(Tag.values())
                .map(tag -> switch (tag) {
                    case Baruch -> pairOf(Tag.Baruch, List.of());
                    case Bmcc -> pairOf(Tag.Bmcc, List.of());
                    case Ccny -> pairOf(Tag.Ccny, List.of());
                    case Columbia -> pairOf(Tag.Columbia, List.of());
                    case Cornell -> pairOf(Tag.Cornell, List.of());
                    case Gwc -> pairOf(Tag.Gwc, List.of());
                    case Hunter -> pairOf(Tag.Hunter, List.of(Tag.Gwc, Tag.MHCPlusPlus));
                    case MHCPlusPlus -> pairOf(Tag.MHCPlusPlus, List.of());
                    case Nyu -> pairOf(Tag.Nyu, List.of());
                    case Patina -> pairOf(Tag.Patina, List.of());
                    case Rpi -> pairOf(Tag.Rpi, List.of());
                    case Sbu -> pairOf(Tag.Sbu, List.of());
                })
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
    }
}
