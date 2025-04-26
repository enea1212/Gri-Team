import './globals.css'
import Navbar from './components/navbar';

export const metadata = {
  title: 'Barber App',
  description: 'Aplicatie MetaMinds',
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <Navbar/>
        {children}
        </body>
    </html>
  );
}
