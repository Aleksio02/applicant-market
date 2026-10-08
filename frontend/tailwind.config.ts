/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        fsp: {
          pink: '#FF0053',
          pinkSoft: '#FFD6E4',
          lilac: '#8A83D1',
          rose: '#FC3777',
          violet: '#310F53',
          purple: '#520978',
          ink: '#1C1D22',
        },
      },
      fontFamily: { sans: ['Montserrat', 'system-ui', 'sans-serif'] },
      boxShadow: { card: '0 20px 60px -20px rgba(49, 15, 83, 0.45)' },
      backgroundImage: {
        'fsp-gradient': 'linear-gradient(135deg, #310F53 0%, #520978 60%, #8A83D1 120%)',
      },
    },
  },
  plugins: [],
}