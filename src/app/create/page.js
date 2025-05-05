'use client';

import '../announcements/announcements.css';
import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function CreateAnnouncementPage() {
  const [titlu, setTitlu] = useState('');
  const [descriere, setDescriere] = useState('');
  const [locatie, setLocatie] = useState('');
  const [pret, setPret] = useState('');
  const [poza, setPoza] = useState(null);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const router = useRouter();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!titlu || !descriere || !locatie || pret <= 0) {
      setError('Toate câmpurile sunt obligatorii (prețul trebuie să fie > 0)');
      return;
    }

    const formData = new FormData();
    formData.append('titlu', titlu);
    formData.append('descriere', descriere);
    formData.append('locatie', locatie);
    formData.append('pret', pret);
    if (poza) {
      formData.append('poza', poza);
    }

    try {
      const token = localStorage.getItem('token');
      const res = await fetch('http://localhost:8080/api/anunturi', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });

      if (!res.ok) {
        const msg = await res.text();
        throw new Error(msg || 'Eroare la creare anunț');
      }

      setSuccess('Anunțul a fost postat cu succes!');
      setError('');
      setTimeout(() => router.push('/myaccount'), 1500);
    } catch (err) {
      setError(err.message);
      setSuccess('');
    }
  };

  return (
    <div className="ann-container">
      <h1 className="ann-title">Postează un Anunț</h1>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
        <input placeholder="Titlu" onChange={e => setTitlu(e.target.value)} />
        <textarea placeholder="Descriere" onChange={e => setDescriere(e.target.value)} rows={4} />
        <input placeholder="Locație" onChange={e => setLocatie(e.target.value)} />
        <input type="number" placeholder="Preț" onChange={e => setPret(e.target.value)} />
        <input type="file" accept="image/*" onChange={e => setPoza(e.target.files[0])} />
        <button className="ann-button" type="submit">Trimite</button>
        {error && <p className="ann-text" style={{ color: 'red' }}>{error}</p>}
        {success && <p className="ann-text" style={{ color: 'green' }}>{success}</p>}
      </form>
    </div>
  );
}
