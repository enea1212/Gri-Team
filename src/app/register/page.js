'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function RegisterPage() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const router = useRouter();

  const handleRegister = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, email, password }),
      });

      const data = await response.json();

      if (response.status === 200) {
        // Successfully registered, redirect to the announcements board
        localStorage.setItem('currentUser', username);
        router.push('/announcemets');
      } else {
        // Display error message from backend
        setError(data || 'Something went wrong');
      }
    } catch (error) {
      setError('Eroare conectare la server!');
      console.error(error);
    }
  };

  return (
    <div className="container">
      <h2>Sign Up</h2>
      <input 
        placeholder="Nume" 
        onChange={e => setUsername(e.target.value)} 
        value={username}
      />
      <input 
        placeholder="Email" 
        onChange={e => setEmail(e.target.value)} 
        value={email}
      />
      <input 
        type="password" 
        placeholder="Parolă" 
        onChange={e => setPassword(e.target.value)} 
        value={password}
      />
      <button onClick={handleRegister}>Register</button>
      {error && <p className="error">{error}</p>}
    </div>
  );
}
