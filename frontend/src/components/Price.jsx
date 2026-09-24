import React from 'react';

export default function Price({ amount, currency = 'INR' }) {
  return (
    <>
      {new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency,
        maximumFractionDigits: 0,
      }).format(Number(amount))}
    </>
  );
}
