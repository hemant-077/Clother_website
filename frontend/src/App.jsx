import React from 'react';
import { ShoppingBag, Sparkles } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useCart } from './context/CartContext.jsx';
import CartPage from './pages/CartPage.jsx';
import ShopPage from './pages/ShopPage.jsx';
import SuccessPage from './pages/SuccessPage.jsx';

function currentRoute() {
  return `${window.location.pathname}${window.location.search}`;
}

export default function App() {
  const [route, setRoute] = useState(currentRoute);
  const { count } = useCart();

  useEffect(() => {
    const onPopState = () => setRoute(currentRoute());
    window.addEventListener('popstate', onPopState);
    return () => window.removeEventListener('popstate', onPopState);
  }, []);

  function navigate(path) {
    window.history.pushState({}, '', path);
    setRoute(currentRoute());
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  const path = route.split('?')[0];

  return (
    <div className="app-shell">
      <header className="topbar">
        <button className="brand" type="button" onClick={() => navigate('/')}>
          <Sparkles size={20} />
          <span>Clothly</span>
        </button>
        <nav className="nav-actions" aria-label="Primary">
          <button className="text-button" type="button" onClick={() => navigate('/')}>
            Shop
          </button>
          <button className="cart-button" type="button" onClick={() => navigate('/cart')}>
            <ShoppingBag size={18} />
            <span>Cart</span>
            <strong>{count}</strong>
          </button>
        </nav>
      </header>

      {path === '/cart' ? (
        <CartPage navigate={navigate} />
      ) : path === '/success' ? (
        <SuccessPage navigate={navigate} />
      ) : (
        <ShopPage navigate={navigate} />
      )}
    </div>
  );
}
