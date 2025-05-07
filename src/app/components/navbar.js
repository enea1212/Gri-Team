'use client';

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import PersonIcon from '@mui/icons-material/AccountCircle';
import './navbar.css';

export default function Navbar() {
  const pathname = usePathname();
  const router = useRouter();
  const [user, setUser] = useState(null);

  useEffect(() => {
    const currentUser = localStorage.getItem('currentUser');
    if (currentUser) setUser(currentUser);
  }, [pathname]);

  const goHome = () => {
    if (user) router.push('/announcements');
    else router.push('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <span onClick={goHome} className="navbar-title">FRESHTRIM</span>
      </div>
      <div className="navbar-center">
        <Link href="/announcements" className="navbar-link">ANUNȚURI</Link>
        <Link href="/about-us" className="navbar-link">DESPRE</Link>
      </div>
      <div className="navbar-right">
        <Link href="/myaccount" className="navbar-link">
        <PersonIcon style={{ fontSize: 24, marginRight: '6px' }} />
        CONT
        </Link>
      </div>
    </nav>
  );
}