package com.patina.codebloom.common.page;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * This class can wrap any object to assign a custom index value. It is most commonly used in combination with
 * {@code Page}. <br>
 * <br>
 * Use <code>Indexed.of()</code> to create this object, or <code>Indexed.ofDefaultList()</code> to generate indexes from
 * a traditional list.
 */
@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class Indexed<T> {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private final int index;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonUnwrapped
    private final T item;

    /**
     * This method will create the Indexed class for you.
     *
     * <p>Keep in mind that the object will be unwrapped, and <code>index</code> will be a property inside of the
     * object.
     *
     * <p>For example, if we had <code>Indexed<User></code>:
     *
     * <pre>
     * <code>
     * {
     *   // index applied from Indexed class
     *   "index": 5,
     *   // rest of the properties from the User class
     *   "id": "32f78d37-8833-42e5-921d-7e57ad5ff3f3",
     *   "name": "John Doe"
     *   // ...
     * }
     * </code>
     * </pre>
     *
     * @param item - The item object
     * @param index - Index of said object
     */
    public static <T> Indexed<T> of(final T item, final int index) {
        return Indexed.<T>builder().item(item).index(index).build();
    }

    /**
     * This method will create a list of {@code Indexed} with default index values generated from the list of items.
     *
     * <p>Index starts from 0.
     */
    public static <T> List<Indexed<T>> ofDefaultList(final List<T> items) {
        return ofDefaultList(items, 0);
    }

    /**
     * This method will create a list of {@code Indexed} with default index values generated from the list of items.
     *
     * <p>Set the starting index to any integer.
     */
    public static <T> List<Indexed<T>> ofDefaultList(final List<T> items, final int startIndex) {
        if (items == null) {
            return List.of();
        }
        return IntStream.range(0, items.size())
                .mapToObj(i -> Indexed.of(items.get(i), i + startIndex))
                .toList();
    }

    /**
     * This method will apply the given function to this {@code Indexed} object, if not null. Otherwise return
     * {@code Indexed} with a null item.
     */
    public <R> Indexed<R> map(final Function<? super T, ? extends R> mapper) {
        final R newItem = (this.item == null) ? null : mapper.apply(this.item);

        return Indexed.of(newItem, this.index);
    }
}
