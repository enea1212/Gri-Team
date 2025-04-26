'use client';
;
import { useRouter } from 'next/navigation';

export default function LandingPage() {
  const router = useRouter();

  return (
    <>
    <div className='landing-background'></div>
      <div className='container'>
      <h1>Bine ai venit la HOLYCUT!</h1>
        <p>Găsește cei mai tari frizeri din zonă</p>
        <p>Pentru a putea face o programare, intră în cont sau înregistrează-te</p>
        <button onClick={() => router.push('/register')}>Sign Up</button>
        <button onClick={() => router.push('/login')}>Log In</button>
      </div>
    </>  
  );
}

