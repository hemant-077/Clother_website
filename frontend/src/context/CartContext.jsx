import React from 'react';
import { createContext, useContext, useMemo, useState } from 'react';


const CartContext = createContext(null);
const STORAGE_KEY = 'clothly_cart';

function readCart() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) ?? [];
  } catch {
    return [];
  }
}

export function CartProvider({ children }) {
  const [items, setItems] = useState(readCart);

  function persist(nextItems) {
    setItems(nextItems);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(nextItems));
  }

  function addItem(product, variant) {
    if (!variant || variant.stockQuantity <= 0) return;
    const nextItems = [...items];
    const existing = nextItems.find((item) => item.variantId === variant.id);
    if (existing) {
      existing.quantity = Math.min(existing.quantity + 1, variant.stockQuantity);
    } else {
      nextItems.push({
        productId: product.id,
        variantId: variant.id,
        name: product.name,
        brand: product.brand,
        price: Number(product.price),
        imageUrl: product.imageUrl,
        sku: variant.sku,
        size: variant.size,
        color: variant.color,
        stockQuantity: variant.stockQuantity,
        quantity: 1,
      });
    }
    persist(nextItems);
  }

  function updateQuantity(variantId, quantity) {
    const nextItems = items
      .map((item) => {
        if (item.variantId !== variantId) return item;
        return {
          ...item,
          quantity: Math.min(Math.max(quantity, 1), item.stockQuantity),
        };
      })
      .filter((item) => item.quantity > 0);
    persist(nextItems);
  }

  function removeItem(variantId) {
    persist(items.filter((item) => item.variantId !== variantId));
  }

  function clearCart() {
    persist([]);
  }

  const summary = useMemo(() => {
    const count = items.reduce((sum, item) => sum + item.quantity, 0);
    const subtotal = items.reduce((sum, item) => sum + item.quantity * item.price, 0);
    return { count, subtotal };
  }, [items]);

  const value = {
    items,
    addItem,
    updateQuantity,
    removeItem,
    clearCart,
    ...summary,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

export function useCart() {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used inside CartProvider');
  }
  return context;
}
