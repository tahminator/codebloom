/**
 * [...] You can iterate through the elements of a set in insertion order.
 *
 * @see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Set
 */
export default class LruCache {
  private set = new Set<string>();

  constructor(private readonly maxSize: number) {}

  has(key: string): boolean {
    return this.set.has(key);
  }

  add(key: string): void {
    if (this.set.has(key)) {
      this.set.delete(key);
    } else if (this.set.size >= this.maxSize) {
      const lru = this.set.values().next().value;
      if (lru) this.set.delete(lru);
    }

    this.set.add(key);
  }

  values() {
    return Array.from(this.set);
  }

  clear() {
    this.set.clear();
  }

  size() {
    return this.set.size;
  }

  [Symbol.iterator](): Iterator<string> {
    return this.set[Symbol.iterator]();
  }
}
