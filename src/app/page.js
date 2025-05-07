'use client';

import { useRouter } from 'next/navigation';

export default function LandingPage() {
  const router = useRouter();

  return (
    <>
    <div className='landing-background'></div>
      <div className='landing-container'>
      <h1>Bine ai venit la FRESHTRIM!</h1>
        <p>
          Găsește frizeri talentați într-un mod rapid și fără prea multe costuri!<br/><br/>
          Crează un cont sau conectează-te pentru a vizualiza anunțuri
          </p>
        <button onClick={() => router.push('/register')}>Sign Up</button>
        <button onClick={() => router.push('/login')}>Log In</button>
      </div>
    </>  
  );
}

