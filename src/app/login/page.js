'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const router = useRouter();

  const handleLogin = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password }),
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || 'Login failed');
      }

      const data = await res.json();
      localStorage.setItem('token', data.token);

      // Verificăm profilul userului după login
      const profileRes = await fetch('http://localhost:8080/api/anunturi/profil', {
        headers: { Authorization: `Bearer ${data.token}` },
      });

      if (!profileRes.ok) throw new Error('Nu s-au putut obține datele profilului');

      const userData = await profileRes.json();
      localStorage.setItem('currentUser', JSON.stringify(userData));

      router.push('/announcements');
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="container">
      <h2>Autentificare</h2>
      <input placeholder="Username" onChange={e => setUsername(e.target.value)} />
      <input type="password" placeholder="Parolă" onChange={e => setPassword(e.target.value)} />
      <button onClick={handleLogin}>Login</button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  );
}
