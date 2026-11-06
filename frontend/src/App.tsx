import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { HashRouter, Route, Routes } from 'react-router-dom';
import { Layout } from './componentes/Layout';
import { NoEncontrado } from './paginas/NoEncontrado';
import { Resultado } from './paginas/Resultado';
import { Seleccion } from './paginas/Seleccion';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: false,
    },
  },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <HashRouter>
        <Layout>
          <Routes>
            <Route path="/" element={<Seleccion />} />
            <Route
              path="/simulacion/:legajo/:planOrigen/:planDestino"
              element={<Resultado />}
            />
            <Route path="*" element={<NoEncontrado />} />
          </Routes>
        </Layout>
      </HashRouter>
    </QueryClientProvider>
  );
}
