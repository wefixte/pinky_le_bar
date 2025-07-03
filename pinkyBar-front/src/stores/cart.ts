import { defineStore } from 'pinia';

export interface CartItem {
  cocktailSizeId: number;
  cocktailId: number;
  cocktailName: string;
  size: string;
  quantity: number;
  price: number;
}

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: [] as CartItem[],
  }),
  getters: {
    totalPrice: (state) =>
      state.items.reduce((sum, item) => sum + item.price * item.quantity, 0),
    totalQuantity: (state) =>
      state.items.reduce((sum, item) => sum + item.quantity, 0),
  },
  actions: {
    addItem(newItem: CartItem) {
      const existing = this.items.find(
        (item) =>
          item.cocktailSizeId === newItem.cocktailSizeId // on compare maintenant par cocktailSizeId
      );
      if (existing) {
        existing.quantity += newItem.quantity;
      } else {
        this.items.push(newItem);
      }
    },
    incrementQuantity(index: number) {
      if (this.items[index]) {
        this.items[index].quantity++;
      }
    },
    decrementQuantity(index: number) {
      if (this.items[index] && this.items[index].quantity > 1) {
        this.items[index].quantity--;
      } else {
        this.removeItem(index);
      }
    },
    removeItem(index: number) {
      this.items.splice(index, 1);
    },
    clear() {
      this.items = [];
    },
  },
});
