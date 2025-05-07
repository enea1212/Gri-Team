'use client';

import { useEffect } from 'react';
import './about.css';

export default function AboutPage() {
    useEffect(() => {
        document.body.style.backgroundColor = '#0f0f0f';
        document.body.style.color = 'white';
    
        return () => {

          document.body.style.backgroundColor = '';
          document.body.style.color = '';
        };
      }, []);

  return (
    <div className="about-container">
      <h1 className="about-title">FRESHTRIM</h1>
      <p className="about-description">
        FRESHTRIM este o platformă modernă care conectează frizerii cu clienții într-un mod simplu și eficient.<br/>
        Frizerii își pot crea anunțuri personalizate cu servicii, prețuri și intervale disponibile.<br/>
        Utilizatorii pot naviga printre frizerii activi, își pot rezerva un interval și pot interacționa într-un mediu prietenos și organizat.<br/>
        Scopul aplicației este să ofere o experiență digitală rapidă și sigură pentru programările de zi cu zi.
        <br/><br/><br/><br/>
        Aceast proiect este un simplu concept care prezintă funcționalitățile de bază ale unei aplicații de frizeri.
        <br/> 
        Echipa MetaMinds: Gri-Team
      </p>
    </div>
  );
}
