'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const router = useRouter();

  const handleLogin = async () => {
    // Clear any previous error message
    setError('');

    // Base64 encode the username and password for the Authorization header
    const encodedCredentials = btoa(`${username}:${password}`);
    
    try {
      // Send POST request to the backend /login endpoint
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: {
          'Authorization': `Basic ${encodedCredentials}`,
          'Content-Type': 'application/json',
        },
      });

      if (!response.ok) {
        // Handle unsuccessful login attempt
        const errorMessage = await response.text();
        setError(errorMessage || 'Failed to log in');
        return;
      }

      // Handle successful login
      const data = await response.json();
      const token = data.token;

      // Store the token (and username if needed) in local storage
      localStorage.setItem('currentUser', username);
      localStorage.setItem('authToken', token);

      // Redirect to the announcemets page
      router.push('/announcemets');
    } catch (error) {
      // Handle any errors that occur during the fetch request
      setError('An error occurred during login. Please try again.');
    }
  };

  return (
    <div className='container'>
      <h2>LOG IN</h2>
      <input
        placeholder="Nume"
        onChange={e => setUsername(e.target.value)}
        value={username}
      />
      <input
        type="password"
        placeholder="Parolă"
        onChange={e => setPassword(e.target.value)}
        value={password}
      />
      <button onClick={handleLogin}>Login</button>
      {error && <p style={{ color: 'red' }}>{error}</p>}
    </div>
  );
}
