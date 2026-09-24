import React from 'react';
import { Plus, Shirt } from 'lucide-react';
import { useMemo, useState } from 'react';
import { useCart } from '../context/CartContext.jsx';
import Price from './Price.jsx';

export default function ProductCard({ product }) {
  const { addItem } = useCart();
  const availableVariants = useMemo(
    () => product.variants.filter((variant) => variant.stockQuantity > 0),
    [product.variants],
  );
  const [selectedVariantId, setSelectedVariantId] = useState(availableVariants[0]?.id ?? product.variants[0]?.id);
  const selectedVariant = product.variants.find((variant) => variant.id === selectedVariantId);

  const groupedSizes = product.variants.reduce((sizes, variant) => {
    if (!sizes.includes(variant.size)) sizes.push(variant.size);
    return sizes;
  }, []);

  function chooseSize(size) {
    const nextVariant = product.variants.find((variant) => variant.size === size && variant.stockQuantity > 0)
      ?? product.variants.find((variant) => variant.size === size);
    setSelectedVariantId(nextVariant?.id);
  }

  return (
    <article className="product-card">
      <div className="product-image-wrap">
        <img src={product.imageUrl} alt={product.name} loading="lazy" />
        <span className="product-category">{product.category}</span>
      </div>
      <div className="product-body">
        <div>
          <p className="eyebrow">{product.brand}</p>
          <h2>{product.name}</h2>
          <p className="description">{product.description}</p>
        </div>

        <div className="meta-row">
          <span>{product.material}</span>
          <span>{product.fit}</span>
        </div>

        <div className="size-row" aria-label={`${product.name} size`}>
          {groupedSizes.map((size) => {
            const hasStock = product.variants.some((variant) => variant.size === size && variant.stockQuantity > 0);
            const active = selectedVariant?.size === size;
            return (
              <button
                className={`size-chip ${active ? 'active' : ''}`}
                type="button"
                key={size}
                disabled={!hasStock}
                onClick={() => chooseSize(size)}
              >
                {size}
              </button>
            );
          })}
        </div>

        <div className="variant-select">
          <label>
            <Shirt size={16} />
            <select
              value={selectedVariantId ?? ''}
              onChange={(event) => setSelectedVariantId(Number(event.target.value))}
            >
              {product.variants.map((variant) => (
                <option value={variant.id} key={variant.id} disabled={variant.stockQuantity <= 0}>
                  {variant.color} / {variant.size} / {variant.stockQuantity} left
                </option>
              ))}
            </select>
          </label>
        </div>

        <div className="card-footer">
          <strong className="price">
            <Price amount={product.price} />
          </strong>
          <button
            className="primary-button"
            type="button"
            disabled={!selectedVariant || selectedVariant.stockQuantity <= 0}
            onClick={() => addItem(product, selectedVariant)}
          >
            <Plus size={18} />
            Add
          </button>
        </div>
      </div>
    </article>
  );
}
