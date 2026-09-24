import React from 'react';
import { Filter, Search, SlidersHorizontal } from 'lucide-react';
import { useEffect, useState } from 'react';
import ProductCard from '../components/ProductCard.jsx';
import { fetchProducts } from '../services/api.js';

const categories = ['', 'Shirts', 'Jackets', 'Trousers', 'Polos', 'Dresses', 'Overshirts'];

export default function ShopPage() {
  const [products, setProducts] = useState([]);
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('');
  const [status, setStatus] = useState('loading');
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    setStatus('loading');
    fetchProducts({ search, category })
      .then((data) => {
        if (!active) return;
        setProducts(data);
        setStatus('ready');
      })
      .catch((exception) => {
        if (!active) return;
        setError(exception.message);
        setStatus('error');
      });
    return () => {
      active = false;
    };
  }, [search, category]);

  return (
    <main>
      <section className="shop-intro">
        <div>
          <p className="eyebrow">New season edit</p>
          <h1>Clothing built for everyday polish.</h1>
        </div>
        <div className="intro-stats" aria-label="Store stats">
          <span>Server priced</span>
          <span>Stock checked</span>
          <span>Stripe ready</span>
        </div>
      </section>

      <section className="toolbar" aria-label="Catalog controls">
        <label className="search-box">
          <Search size={18} />
          <input
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Search shirts, jackets, dresses"
          />
        </label>
        <label className="select-box">
          <Filter size={18} />
          <select value={category} onChange={(event) => setCategory(event.target.value)}>
            {categories.map((item) => (
              <option value={item} key={item || 'all'}>
                {item || 'All categories'}
              </option>
            ))}
          </select>
        </label>
      </section>

      {status === 'loading' && (
        <div className="state-panel">
          <SlidersHorizontal size={22} />
          <span>Loading catalog...</span>
        </div>
      )}

      {status === 'error' && (
        <div className="state-panel error">
          <span>{error}</span>
        </div>
      )}

      {status === 'ready' && products.length === 0 && (
        <div className="state-panel">
          <span>No matching products.</span>
        </div>
      )}

      {status === 'ready' && products.length > 0 && (
        <section className="product-grid" aria-label="Products">
          {products.map((product) => (
            <ProductCard product={product} key={product.id} />
          ))}
        </section>
      )}
    </main>
  );
}
