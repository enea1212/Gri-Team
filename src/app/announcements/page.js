'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import './announcements.css';

export default function AnnouncementsPage() {
  const [status, setStatus] = useState('loading');
  const [user, setUser] = useState(null);
  const [anunturi, setAnunturi] = useState([]);
  const [error, setError] = useState('');
  const [selectedId, setSelectedId] = useState(null);
  const [disponibilitate, setDisponibilitate] = useState({});
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      setError('Trebuie să fii autentificat pentru a vedea anunțurile.');
      return;
    }

    fetch('http://localhost:8080/api/anunturi/public', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => {
        if (!res.ok) throw new Error('Eroare la încărcarea anunțurilor');
        return res.json();
      })
      .then(data => setAnunturi(data))
      .catch(err => setError(err.message));
  }, []);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      setStatus('unauthenticated');
      return;
    }

    fetch('http://localhost:8080/api/anunturi/profil', {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then(res => {
        if (!res.ok) throw new Error('Not verified');
        return res.json();
      })
      .then(data => {
        setUser(data.user); // presupunem că userul e într-un field `user`
        setStatus('verified');
      })
      .catch(() => {
        setStatus('unauthenticated');
      });
  }, []);

  const toggleDisponibilitate = async (anuntId) => {
    if (selectedId === anuntId) {
      setSelectedId(null);
      return;
    }

    try {
      const token = localStorage.getItem('token');
      const res = await fetch(`http://localhost:8080/api/anunturi/${anuntId}/ore-disponibile`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!res.ok) throw new Error('Eroare la încărcarea programărilor');

      const data = await res.json();
      console.log('Disponibilitate pentru anunțul', anuntId, ':', data);
      setDisponibilitate(prev => ({ ...prev, [anuntId]: data }));
      setSelectedId(anuntId);
    } catch (err) {
      setError(err.message);
    }
  };

  const handleRezervare = async (anuntId, interval) => {
    try {
      const token = localStorage.getItem('token');
      const formData = new URLSearchParams();
      formData.append('anuntId', anuntId);
      formData.append('interval', interval);
  
      const res = await fetch('http://localhost:8080/api/anunturi/rezervare', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: formData.toString(),
      });
  
      const text = await res.text();
  
      if (res.ok) {
        alert('Rezervare efectuată cu succes!');
        // Reîncarcă intervalele
        toggleDisponibilitate(anuntId);
      } else {
        alert('Eroare: ' + text);
      }
    } catch (err) {
      alert('Eroare la rezervare');
    }
  };
  

  return (
    <div className="ann-container">

      {status === 'unauthenticated' && (
        <p style={{ color: 'red' }}>
          Trebuie să ai un cont verificat pentru a posta un anunț.
        </p>
      )}

      {status === 'verified' && (
        <button className="post-ann-button" onClick={() => router.push('/create')}>
          Postează un anunț
        </button>
      )}

      <h1 className="ann-title">Anunțuri disponibile</h1>

      {error && <p className="ann-text" style={{ color: 'red' }}>{error}</p>}

      {anunturi.map(anunt => (
        <div key={anunt.id} className="ann-infolist" style={{ borderBottom: '1px solid #ccc', paddingBottom: '1rem', marginBottom: '1.5rem' }}>
          {anunt.poza && (
            <img
              src={`http://localhost:8080/uploads/${anunt.poza}`}
              alt="Poza frizer"
              className="ann-img"
            />
          )}
          <h3 style={{ fontWeight: 'bold', fontSize: '1.6rem'}}>
            {anunt.titlu}
          </h3>
          <p><strong>Descriere:</strong> {anunt.descriere}</p>
          <p><strong>Locație:</strong> {anunt.locatie}</p>
          <p><strong>Preț:</strong> {anunt.pret} RON</p>
          <p><strong>Frizer:</strong> {anunt.user.username}</p>

          <button className="ann-button" onClick={() => toggleDisponibilitate(anunt.id)}>
            {selectedId === anunt.id ? 'Ascunde Programările' : 'Programează-te'}
          </button>

          {selectedId === anunt.id && disponibilitate[anunt.id] && (
            <ul className="ann-infolist">
              {disponibilitate[anunt.id].length > 0 ? (
                disponibilitate[anunt.id].map((interval, index) => (
                  <li key={index} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <span>{interval}</span>
                    <button
                      className="ann-button"
                      style={{ fontSize: '0.75rem', padding: '0.3rem 0.7rem', marginLeft: '1rem' }}
                      onClick={() => handleRezervare(anunt.id, interval)}
                    >
                      Rezervă
                    </button>
                  </li>
                ))
              ) : (
                <li>Nu există programări disponibile.</li>
              )}
            </ul>
          )}

        </div>
      ))}
    </div>
  );
}
