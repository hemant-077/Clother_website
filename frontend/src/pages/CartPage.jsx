import React from 'react';
import { ArrowLeft, ArrowRight, CreditCard, Minus, Plus, Trash2 } from 'lucide-react';
import { useState } from 'react';
import Price from '../components/Price.jsx';
import { useCart } from '../context/CartContext.jsx';
import { createCheckout } from '../services/api.js';

const initialCustomer = {
  name: '',
  email: '',
  phone: '',
  address: '',
  city: '',
  state: '',
  postalCode: '',
  country: 'India',
};

export default function CartPage({ navigate }) {
  const { items, subtotal, updateQuantity, removeItem } = useCart();
  const [customer, setCustomer] = useState(initialCustomer);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  function updateCustomer(field, value) {
    setCustomer((current) => ({ ...current, [field]: value }));
  }

  async function handleCheckout(event) {
    event.preventDefault();
    if (items.length === 0) return;
    setError('');
    setSubmitting(true);
    try {
      const response = await createCheckout({
        customer,
        items: items.map((item) => ({
          variantId: item.variantId,
          quantity: item.quantity,
        })),
        successUrl: `${window.location.origin}/success`,
        cancelUrl: `${window.location.origin}/cart`,
      });
      window.location.href = response.checkoutUrl;
    } catch (exception) {
      setError(exception.message);
      setSubmitting(false);
    }
  }

  return (
    <main className="cart-layout">
      <section className="cart-list">
        <button className="link-button" type="button" onClick={() => navigate('/')}>
          <ArrowLeft size={18} />
          Continue shopping
        </button>
        <div className="section-heading">
          <p className="eyebrow">Bag</p>
          <h1>{items.length ? `${items.length} item${items.length > 1 ? 's' : ''}` : 'Your cart is empty'}</h1>
        </div>

        {items.length === 0 ? (
          <div className="state-panel">
            <span>Fresh pieces are waiting in the catalog.</span>
          </div>
        ) : (
          <div className="line-items">
            {items.map((item) => (
              <article className="cart-line" key={item.variantId}>
                <img src={item.imageUrl} alt={item.name} />
                <div className="line-copy">
                  <p className="eyebrow">{item.brand}</p>
                  <h2>{item.name}</h2>
                  <p>{item.color} / {item.size} / {item.sku}</p>
                  <strong>
                    <Price amount={item.price} />
                  </strong>
                </div>
                <div className="quantity-control" aria-label={`${item.name} quantity`}>
                  <button type="button" onClick={() => updateQuantity(item.variantId, item.quantity - 1)}>
                    <Minus size={16} />
                  </button>
                  <span>{item.quantity}</span>
                  <button type="button" onClick={() => updateQuantity(item.variantId, item.quantity + 1)}>
                    <Plus size={16} />
                  </button>
                </div>
                <button className="icon-button" type="button" onClick={() => removeItem(item.variantId)}>
                  <Trash2 size={18} />
                </button>
              </article>
            ))}
          </div>
        )}
      </section>

      <form className="checkout-panel" onSubmit={handleCheckout}>
        <div className="section-heading compact">
          <p className="eyebrow">Checkout</p>
          <h2>Shipping details</h2>
        </div>
        <div className="form-grid">
          {Object.entries({
            name: 'Full name',
            email: 'Email',
            phone: 'Phone',
            address: 'Address',
            city: 'City',
            state: 'State',
            postalCode: 'Postal code',
            country: 'Country',
          }).map(([field, label]) => (
            <label className={field === 'address' ? 'wide' : ''} key={field}>
              <span>{label}</span>
              <input
                required
                type={field === 'email' ? 'email' : 'text'}
                value={customer[field]}
                onChange={(event) => updateCustomer(field, event.target.value)}
              />
            </label>
          ))}
        </div>

        <div className="summary-box">
          <div>
            <span>Subtotal</span>
            <strong><Price amount={subtotal} /></strong>
          </div>
          <div>
            <span>Total</span>
            <strong><Price amount={subtotal} /></strong>
          </div>
        </div>

        {error && <p className="form-error">{error}</p>}

        <button className="checkout-button" type="submit" disabled={items.length === 0 || submitting}>
          <CreditCard size={18} />
          <span>{submitting ? 'Opening checkout...' : 'Pay with Stripe'}</span>
          <ArrowRight size={18} />
        </button>
      </form>
    </main>
  );
}
