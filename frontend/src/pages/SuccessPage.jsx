import React from 'react';
import { ArrowLeft, CheckCircle2, PackageCheck } from 'lucide-react';
import { useEffect, useState } from 'react';
import Price from '../components/Price.jsx';
import { useCart } from '../context/CartContext.jsx';
import { fetchOrder } from '../services/api.js';

export default function SuccessPage({ navigate }) {
  const params = new URLSearchParams(window.location.search);
  const orderNumber = params.get('order');
  const checkoutSessionId = params.get('session_id');
  const { clearCart } = useCart();
  const [order, setOrder] = useState(null);
  const [status, setStatus] = useState(orderNumber ? 'loading' : 'missing');
  const [error, setError] = useState('');

  useEffect(() => {
    if (!orderNumber) return;
    let active = true;
    fetchOrder(orderNumber, { sessionId: checkoutSessionId ?? '' })
      .then((data) => {
        if (!active) return;
        setOrder(data);
        setStatus('ready');
        if (data.status === 'PAID') {
          clearCart();
        }
      })
      .catch((exception) => {
        if (!active) return;
        setError(exception.message);
        setStatus('error');
      });
    return () => {
      active = false;
    };
  }, [orderNumber, checkoutSessionId]);

  return (
    <main className="success-page">
      <button className="link-button" type="button" onClick={() => navigate('/')}>
        <ArrowLeft size={18} />
        Back to shop
      </button>

      {status === 'loading' && (
        <div className="state-panel">
          <PackageCheck size={22} />
          <span>Loading order...</span>
        </div>
      )}

      {status === 'missing' && (
        <div className="state-panel error">
          <span>Order number missing.</span>
        </div>
      )}

      {status === 'error' && (
        <div className="state-panel error">
          <span>{error}</span>
        </div>
      )}

      {status === 'ready' && order && (
        <section className="receipt">
          <div className="receipt-heading">
            <CheckCircle2 size={34} />
            <div>
              <p className="eyebrow">{order.status}</p>
              <h1>Order {order.orderNumber}</h1>
            </div>
          </div>
          <div className="receipt-lines">
            {order.items.map((item) => (
              <div className="receipt-line" key={item.sku}>
                <span>{item.productName} / {item.size} / {item.color} x {item.quantity}</span>
                <strong><Price amount={item.lineTotal} /></strong>
              </div>
            ))}
          </div>
          <div className="receipt-total">
            <span>Total</span>
            <strong><Price amount={order.total} currency={order.currency.toUpperCase()} /></strong>
          </div>
          {order.stripePaymentIntentId && (
            <div className="receipt-meta">
              <span>Payment Intent ID</span>
              <strong>{order.stripePaymentIntentId}</strong>
            </div>
          )}
        </section>
      )}
    </main>
  );
}
