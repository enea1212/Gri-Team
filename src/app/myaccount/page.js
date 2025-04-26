'use client';

import { useEffect, useState } from 'react';

export default function MyAccount() {
  const [user, setUser] = useState(null);

  useEffect(() => {
    const current = localStorage.getItem('currentUser');
    if (current) setUser(current);
  }, []);

  return (
    <div className="container">
      <h2>Contul meu</h2>
      {user ? (
        <p>Bun venit, <strong>{user}</strong>!</p>
      ) : (
        <>
          <p>Nu sunteti logat la acest moment.</p>
          <a href="/login"><button>Login</button></a>
          <a href="/register"><button>Sign Up</button></a>
        </>
      )}
    </div>
  );
}
