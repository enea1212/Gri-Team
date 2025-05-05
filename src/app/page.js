'use client';

import { useRouter } from 'next/navigation';

export default function LandingPage() {
  const router = useRouter();

  return (
    <>
    <div className='landing-background'></div>
      <div className='container'>
      <h1>Bine ai venit la HOLYCUT!</h1>
        <p>Crează un cont sau conectează-te pentru a vizualiza anunțuri</p>
        <button onClick={() => router.push('/register')}>Sign Up</button>
        <button onClick={() => router.push('/login')}>Log In</button>
      </div>
    </>  
  );
}

