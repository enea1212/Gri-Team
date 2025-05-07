'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import PersonIcon from '@mui/icons-material/AccountCircle';
import DescriptionIcon from '@mui/icons-material/Description';
import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import '../announcements/announcements.css';

export default function MyAccountPage() {
  const [profil, setProfil] = useState(null);
  const [error, setError] = useState('');
  const [deleted, setDeleted] = useState(false);
  const [programariPrimite, setProgramariPrimite] = useState([]);
  const [rezervarileMele, setRezervarileMele] = useState([]);

  const router = useRouter();


  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      setError('Nu ești autentificat.');
      return;
    }

    fetch('http://localhost:8080/api/anunturi/profil', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => {
        if (!res.ok) throw new Error('Eroare la încărcarea datelor');
        return res.json();
      })
      .then(data => {
        console.log('Date profil:', data);
        setProfil(data);

        if (data.anunturiRezervate) {
          setRezervarileMele(data.anunturiRezervate);
        }
        
      })
      .catch(err => {
        setError(err.message);
      });
  }, [deleted]);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token || !profil || !profil.anuntPublicat) return;
  
    fetch(`http://localhost:8080/api/anunturi/${profil.anuntPublicat.id}/programariVIEW`, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => {
        if (!res.ok) throw new Error('Eroare la programările primite');
        return res.json();
      })
      .then(data => {
        setProgramariPrimite(data);
        console.log('Programări primite:', data); // 🔥 aici vezi exact structura
      })
      .catch(err => console.error('Programări primite – eroare:', err.message));
  }, [profil]);

  const handleDelete = async () => {
    const token = localStorage.getItem('token');
    if (!profil || !profil.anuntPublicat) return;

    try {
      const res = await fetch(`http://localhost:8080/api/anunturi/${profil.anuntPublicat.id}`, {
        method: 'DELETE',
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error('Nu s-a putut șterge anunțul');

      setProfil({ ...profil, anuntPublicat: null });
      setDeleted(true);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    router.push('/');
  };
  
  const handleAnulareRezervare = async (anuntId, interval) => {
    try {
      const token = localStorage.getItem('token');
      const body = new URLSearchParams();
      body.append('anuntId', anuntId);
      body.append('interval', interval);
  
      const res = await fetch('http://localhost:8080/api/anunturi/anulare-rezervare', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: body.toString(),
      });
  
      const msg = await res.text();
      if (res.ok) {
        alert('Rezervare anulată!');
        // Reîncarcă datele de profil
        window.location.reload();
      } else {
        alert('Eroare: ' + msg);
      }
    } catch (err) {
      alert('Eroare la anulare');
    }
  };
  

  return (
    <>
    <div className='myacc-background'></div>
      <div className="ann-container">
        <h1 className="ann-title">Profilul Meu</h1>

        {error && (
          <div className="ann-text" style={{ textAlign: 'center' }}>
            <p style={{ color: 'red' }}>{error}</p>
            <button
              className="ann-button"
              onClick={() => router.push('/')}
              style={{ backgroundColor: '#e0e7ff', color: '#1e3a8a', borderColor: '#93c5fd' }}
            >
              Înapoi la pagina principală
            </button>
          </div>
        )}

        {profil && (
          <>
            <p className="ann-text"><PersonIcon style={{ fontSize: 30, marginRight: '6px' }} /><strong>Username:</strong> {profil.utilizator.username}</p>
            <button
              className="ann-button"
              onClick={handleLogout}
              style={{ backgroundColor: '#fee2e2', color: '#b91c1c', borderColor: '#fca5a5', marginBottom: '1rem' }}
            >
              Deconectează-te
            </button>
          </>
        )}

        {profil && profil.anuntPublicat ? (
          <div className="ann-infolist">
            <h3 style={{ marginTop: '2rem' }}>
            <DescriptionIcon style={{ marginRight: '8px', verticalAlign: 'middle', color: '#4f46e5' }} />
              Anunțurile mele:
            </h3>
            {profil.anuntPublicat.poza && (
              <img
                src={`http://localhost:8080/uploads/${profil.anuntPublicat.poza}`}
                alt="Poza anunț"
                className='ann-img'
              />
            )}

            <p><strong>Titlu:</strong> {profil.anuntPublicat.titlu}</p>
            <p><strong>Descriere:</strong> {profil.anuntPublicat.descriere}</p>
            <p><strong>Locație:</strong> {profil.anuntPublicat.locatie}</p>
            <p><strong>Preț:</strong> {profil.anuntPublicat.pret} RON</p>        

            <button className="ann-button" onClick={handleDelete}>Șterge anunțul</button>
          </div>
        ) : (
          <p className="ann-text">Nu ai niciun anunț postat.</p>
        )}

          <h3 style={{ marginTop: '2rem' }}>
          <EventAvailableIcon style={{ marginRight: '8px', verticalAlign: 'middle', color: '#22c55e' }} />
            Programări primite
          </h3>

          {programariPrimite.length > 0 ? (
            <ul className="ann-infolist">
              {programariPrimite.map((p, i) => (
                <li key={i}>
                  Ora: <strong>{p.intervalOrar.substring(0, 5)}</strong> — rezervat de: <strong>{p.reservedByUserName}</strong>
                </li>
              ))}
            </ul>
          ) : (
            <p className="ann-text">Nicio programare primită momentan.</p>
          )}


          <h3 style={{ marginTop: '2rem' }}>
          <CalendarMonthIcon style={{ marginRight: '8px', verticalAlign: 'middle', color: '#8b5cf6' }} />
            Programările mele
          </h3>

          {rezervarileMele.length > 0 ? (
            <ul className="ann-infolist">
              {rezervarileMele.map((rez, index) => (
                <li key={index} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span>
                    Ora: <strong>{rez.intervalRezervat.substring(0, 5)}</strong> — Anunț: <strong>{rez.anunt.titlu}</strong> — Frizer: <strong>{rez.anunt.user?.username || 'N/A'}</strong>
                  </span>
                  <button
                    className="ann-button"
                    style={{ fontSize: '0.75rem', padding: '0.3rem 0.7rem', marginLeft: '1rem' }}
                    onClick={() => handleAnulareRezervare(rez.anunt.id, rez.intervalRezervat.substring(0, 5))}
                  >
                    Anulează
                  </button>
                </li>
              ))}
            </ul>
          ) : (
            <p className="ann-text">Nu ai rezervări active.</p>
          )}


      </div>
    </>  
  );
}
