'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import LocationOnIcon from '@mui/icons-material/LocationOn'
//import CircularProgress from '@mui/material/CircularProgress';
import './announcements.css'

export default function AnnouncementsPage() {
  const [user, setUser] = useState(null);
  //const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    const currentUser = localStorage.getItem('currentUser');
    if (!currentUser) {
      router.push('/');
    } else {
      setUser(currentUser);
    }
  }, []);
 
/*  
  if (loading) {
    return (
      <div className="loading-spinner">
        <CircularProgress color="primary" />
      </div>
    );
  }
*/

  return (
    
    <div className='announcement-container'>
      <div className='announcement-photo'></div>

    <div className='announcement-content'>
      <h1 className='announcement-title'>Titlu</h1>
      <h1 className='announcement-desc'>Descriere</h1>
      <div className='announcement-location'>
          <LocationOnIcon style={{ color: '#0070f3', marginRight: '8px' }} />
          <span>Bucuresti, Romania</span>
        </div>
      <button className='book-button'>Programează-te</button>
    </div>
  </div>

  );
}
