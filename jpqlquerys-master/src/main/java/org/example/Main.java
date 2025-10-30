package org.example;

import funciones.FuncionApp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("example-unit");
            EntityManager em = emf.createEntityManager();

            // Persistir la entidad UnidadMedida en estado "gestionada"
            em.getTransaction().begin();
            // Crear una nueva entidad UnidadMedida en estado "nueva"
            UnidadMedida unidadMedida = UnidadMedida.builder()
                    .denominacion("Kilogramo")
                    .build();
            UnidadMedida unidadMedidapote = UnidadMedida.builder()
                    .denominacion("pote")
                    .build();

            em.persist(unidadMedida);
            em.persist(unidadMedidapote);


            // Crear una nueva entidad Categoria en estado "nueva"
            Categoria categoria = Categoria.builder()
                    .denominacion("Frutas")
                    .esInsumo(true)
                    .build();

            // Crear una nueva entidad Categoria en estado "nueva"
            Categoria categoriaPostre = Categoria.builder()
                    .denominacion("Postre")
                    .esInsumo(false)
                    .build();

            // Persistir la entidad Categoria en estado "gestionada"

            em.persist(categoria);
            em.persist(categoriaPostre);


            // Crear una nueva entidad ArticuloInsumo en estado "nueva"
            ArticuloInsumo articuloInsumo = ArticuloInsumo.builder()
                    .denominacion("Manzana")
                    .codigo(UUID.randomUUID().toString())
                    .precioCompra(1.5)
                    .precioVenta(5d)
                    .stockActual(100)
                    .stockMaximo(200)
                    .esParaElaborar(true)
                    .unidadMedida(unidadMedida)
                    .build();


            ArticuloInsumo articuloInsumoPera = ArticuloInsumo.builder()
                    .denominacion("Pera")
                    .codigo(UUID.randomUUID().toString())
                    .precioCompra(2.5)
                    .precioVenta(10d)
                    .stockActual(130)
                    .stockMaximo(200)
                    .esParaElaborar(true)
                    .unidadMedida(unidadMedida)
                    .build();

            // Persistir la entidad ArticuloInsumo en estado "gestionada"

            em.persist(articuloInsumo);
            em.persist(articuloInsumoPera);

            Imagen manza1 = Imagen.builder().denominacion("Manzana Verde").
                    build();
            Imagen manza2 = Imagen.builder().denominacion("Manzana Roja").
                    build();

            Imagen pera1 = Imagen.builder().denominacion("Pera Verde").
                    build();
            Imagen pera2 = Imagen.builder().denominacion("Pera Roja").
                    build();


            // Agregar el ArticuloInsumo a la Categoria
            categoria.getArticulos().add(articuloInsumo);
            categoria.getArticulos().add(articuloInsumoPera);
            // Actualizar la entidad Categoria en la base de datos

            // em.merge(categoria);

            // Crear una nueva entidad ArticuloManufacturadoDetalle en estado "nueva"
            ArticuloManufacturadoDetalle detalleManzana = ArticuloManufacturadoDetalle.builder()
                    .cantidad(2)
                    .articuloInsumo(articuloInsumo)
                    .build();


            ArticuloManufacturadoDetalle detallePera = ArticuloManufacturadoDetalle.builder()
                    .cantidad(2)
                    .articuloInsumo(articuloInsumoPera)
                    .build();

            // Crear una nueva entidad ArticuloManufacturado en estado "nueva"
            ArticuloManufacturado articuloManufacturado = ArticuloManufacturado.builder()
                    .denominacion("Ensalada de frutas")
                    .descripcion("Ensalada de manzanas y peras ")
                    .precioVenta(150d)
                    .tiempoEstimadoMinutos(10)
                    .preparacion("Cortar las frutas en trozos pequeños y mezclar")
                    .unidadMedida(unidadMedidapote)
                    .build();

            articuloManufacturado.getImagenes().add(manza1);
            articuloManufacturado.getImagenes().add(pera1);

            categoriaPostre.getArticulos().add(articuloManufacturado);
            // Crear una nueva entidad ArticuloManufacturadoDetalle en estado "nueva"

            // Agregar el ArticuloManufacturadoDetalle al ArticuloManufacturado
            articuloManufacturado.getDetalles().add(detalleManzana);
            articuloManufacturado.getDetalles().add(detallePera);
            // Persistir la entidad ArticuloManufacturado en estado "gestionada"
            categoriaPostre.getArticulos().add(articuloManufacturado);
            em.persist(articuloManufacturado);
            em.getTransaction().commit();

            // modificar la foto de manzana roja
            em.getTransaction().begin();
            articuloManufacturado.getImagenes().add(manza2);
            em.merge(articuloManufacturado);
            em.getTransaction().commit();

            //creo y guardo un cliente
            em.getTransaction().begin();
            Cliente cliente = Cliente.builder()
                    .cuit(FuncionApp.generateRandomCUIT())
                    .razonSocial("Juan Perez")
                    .build();
            em.persist(cliente);

            Cliente cliente2 = Cliente.builder()
                    .cuit(FuncionApp.generateRandomCUIT())
                    .razonSocial("Santiago")
                    .build();
            em.persist(cliente2);

            em.getTransaction().commit();

            //creo y guardo una factura
            em.getTransaction().begin();

            FacturaDetalle detalle1 = new FacturaDetalle(3, articuloInsumo);
            detalle1.calcularSubTotal();
            FacturaDetalle detalle2 = new FacturaDetalle(2, articuloInsumoPera);
            detalle2.calcularSubTotal();
            FacturaDetalle detalle3 = new FacturaDetalle(2, articuloManufacturado);
            detalle3.calcularSubTotal();

            Factura factura = Factura.builder()
                    .puntoVenta(2024)
                    .fechaAlta(new Date())
                    .fechaComprobante(FuncionApp.generateRandomDate())
                    .cliente(cliente)
                    .nroComprobante(FuncionApp.generateRandomNumber())
                    .build();
            factura.addDetalleFactura(detalle1);
            factura.addDetalleFactura(detalle2);
            factura.addDetalleFactura(detalle3);
            factura.calcularTotal();

            em.persist(factura);
            em.getTransaction().commit();

            // Crear la consulta SQL nativa
            // Crear la consulta JPQL

            String jpql = "SELECT am FROM ArticuloManufacturado am LEFT JOIN FETCH am.detalles d WHERE am.id = :id";
            Query query = em.createQuery(jpql);
            query.setParameter("id", 3L);
            ArticuloManufacturado articuloManufacturadoCon = (ArticuloManufacturado) query.getSingleResult();

            System.out.println("Artículo manufacturado: " + articuloManufacturado.getDenominacion());
            System.out.println("Descripción: " + articuloManufacturado.getDescripcion());
            System.out.println("Tiempo estimado: " + articuloManufacturado.getTiempoEstimadoMinutos() + " minutos");
            System.out.println("Preparación: " + articuloManufacturado.getPreparacion());

            System.out.println("Líneas de detalle:");
            for (ArticuloManufacturadoDetalle detalle : articuloManufacturado.getDetalles()) {
                System.out.println("- " + detalle.getCantidad() + " unidades de " + detalle.getArticuloInsumo().getDenominacion());

            }

            //   em.getTransaction().begin();
            //   em.remove(articuloManufacturado);
            //   em.getTransaction().commit();

            // EJERCICIO 1. Listar todos los clientes
            System.out.println("\n=== LISTADO DE TODOS LOS CLIENTES ===");
            String jpqlClientes = "SELECT c FROM Cliente c";
            Query queryClientes = em.createQuery(jpqlClientes);
            List<Cliente> clientes = queryClientes.getResultList();

            for (Cliente c : clientes) {
                System.out.println("Cliente: " + c.getRazonSocial() + " - CUIT: " + c.getCuit());
            }

            // EJERCICIO 2. Listar todas las facturas generadas en el último mes
            System.out.println("\n=== FACTURAS DEL ÚLTIMO MES ===");
            // Calcular la fecha de hace un mes
            Date fechaHaceUnMes = new Date(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
            String jpqlFacturas = "SELECT f FROM Factura f WHERE f.fechaAlta >= :fechaInicio ORDER BY f.fechaAlta DESC";
            Query queryFacturas = em.createQuery(jpqlFacturas);
            queryFacturas.setParameter("fechaInicio", fechaHaceUnMes);
            List<Factura> facturas = queryFacturas.getResultList();
            for (Factura f : facturas) {
                System.out.println("Factura N°: " + f.getNroComprobante() +
                        " - Cliente: " + f.getCliente().getRazonSocial() +
                        " - Fecha: " + f.getFechaAlta() +
                        " - Total: $" + f.getTotal());
            }

            // EJERCICIO 3. Obtener el cliente que ha generado más facturas
            String jpqlFactura = "SELECT f.cliente " +
                    "FROM Factura f " +
                    "GROUP BY f.cliente " +
                    "ORDER BY COUNT(f) DESC";
            TypedQuery<Cliente> query1 = em.createQuery(jpqlFactura, Cliente.class);
            query.setMaxResults(1);
            List<Cliente> listaCliente = query1.getResultList();
            System.out.println("\n=== CLIENTE QUE GENERÓ MÁS FACTURAS ===");
            if (!listaCliente.isEmpty()) {
                System.out.println("El cliente que ha generado más facturas es: " + listaCliente.get(0));
            } else {
                System.out.println("No hay facturas registradas.");
            }


            // EJERCICIO 4. Productos más vendidos, ordenados por la cantidad total vendida.
            String jpqlProd = "SELECT a, SUM(fd.cantidad) " +
                    "FROM FacturaDetalle fd " +
                    "JOIN fd.articulo a " +
                    "GROUP BY a " +
                    "ORDER BY SUM(fd.cantidad) DESC";
            List<Object[]> resultados = em.createQuery(jpqlProd, Object[].class).getResultList();
            System.out.println("\n=== PRODUCTOS MÁS VENDIDOS ORDENADOS POR LA CANTIDAD TOTAL VENDIDA ===");
            for (Object[] fila : resultados) {
                Articulo art = (Articulo) fila[0];
                Double totalVendidas = (Double) fila[1];

                System.out.println("Artículo: " + art.getDenominacion() +
                        " | Total vendidas: " + totalVendidas.intValue());
            }

            // EJERCICIO 5. Consultar las facturas emitidas en los 3 últimos meses de un cliente específico
            // Usamos el cliente 'Juan Perez' para las consultas.
            Long clienteIdEspecifico = cliente.getId();
            System.out.println("\n=== FACTURAS DE LOS ÚLTIMOS 3 MESES ===");
            //Calculamos la fecha límite
            LocalDate fechaLimite = LocalDate.now().minus(3, ChronoUnit.MONTHS);
            String jpqlFacturas3Meses = "SELECT f FROM Factura f WHERE f.cliente.id = :clienteId AND f.fechaComprobante >= :fechaLimite";
            TypedQuery<Factura> queryFacturas3Meses = em.createQuery(jpqlFacturas3Meses, Factura.class);
            //Establezco parámetros
            queryFacturas3Meses.setParameter("clienteId", clienteIdEspecifico);
            queryFacturas3Meses.setParameter("fechaLimite", fechaLimite);
            //Ejecutamos
            List<Factura> facturas3Meses = queryFacturas3Meses.getResultList();
            System.out.println("Facturas del Cliente ID " + clienteIdEspecifico + " (Juan Perez) emitidas desde: " + fechaLimite);
            if (facturas3Meses.isEmpty()) {
                System.out.println("No se encontraron facturas en el período.");
            } else {
                for (Factura f : facturas3Meses) {
                    System.out.println(" - Nro: " + f.getNroComprobante() + " | Monto: $" + f.getTotal() + " | Fecha: " + f.getFechaComprobante());
                }
            }

            // EJERCICIO 6. Calcular el monto total facturado por un cliente
            System.out.println("\n=== MONTO TOTAL FACTURADO POR CLIENTE ===");
            String jpqlMontoTotal = "SELECT SUM(f.total) FROM Factura f WHERE f.cliente.id = :clienteId";
            TypedQuery<Double> queryMontoTotal = em.createQuery(jpqlMontoTotal, Double.class);
            queryMontoTotal.setParameter("clienteId", clienteIdEspecifico);
            Double montoTotal = queryMontoTotal.getSingleResult();
            if (montoTotal == null) {
                System.out.println("Cliente ID: " + clienteIdEspecifico + " -> Monto Total Facturado: $0.00");
            } else {
                System.out.printf("Cliente ID: %d -> Monto Total Facturado: $%.2f%n", clienteIdEspecifico, montoTotal);
            }

            // EJERCICIO 7. Listar los Artículos vendidos en una factura
            Long idFactura = 1L;
            String jpql7 = "SELECT fd.articulo FROM FacturaDetalle fd WHERE fd.factura.id = :idFactura";
            Query q7 = em.createQuery(jpql7);
            q7.setParameter("idFactura", idFactura);
            System.out.println("\n=== ARTÍCULOS VENDIDOS EN LA FACTURA CON ID 1===");
            List<Articulo> articulos = q7.getResultList();
            articulos.forEach(a -> System.out.println("- " + a.getDenominacion()));

            // EJERCICIO 8. Obtener el Artículo más caro vendido en una factura
            String jpql8 = "SELECT fd.articulo FROM FacturaDetalle fd WHERE fd.factura.id = :idFactura ORDER BY fd.articulo.precioVenta DESC";
            Query q8 = em.createQuery(jpql8);
            q8.setParameter("idFactura", idFactura);
            q8.setMaxResults(1);
            Articulo masCaro = (Articulo) q8.getSingleResult();
            System.out.println("\n=== ARTÍCULO MÁS CARO VENDIDO EN UNA FACTURA ===");
            System.out.println("Más caro: " + masCaro.getDenominacion() + " ($" + masCaro.getPrecioVenta()+")");

            // EJERCICIO 9. Contar la cantidad total de facturas generadas en el sistema
            System.out.println("\n=== TOTAL DE FACTURAS GENERADAS ===");
            String jpqlTotalFacturas = "SELECT COUNT(f) FROM Factura f";
            Query queryTotalFacturas = em.createQuery(jpqlTotalFacturas);
            Long totalFacturas = (Long) queryTotalFacturas.getSingleResult();
            System.out.println("Total de facturas generadas: " + totalFacturas);

            // EJERCICIO 10. Listar las facturas cuyo total es mayor a un valor determinado
            System.out.println("\n=== FACTURAS CON TOTAL MAYOR A $1000 ===");
            double valorMinimo = 1000.0;
            System.out.println("Mostrando facturas con total mayor a $" + valorMinimo);
            TypedQuery<Object[]> queryFacturasMayores = em.createQuery(
                    "SELECT f.nroComprobante, SUM(df.cantidad * a.precioVenta) AS total " +
                            "FROM Factura f " +
                            "JOIN f.detallesFactura df " +
                            "JOIN df.articulo a " +
                            "GROUP BY f.nroComprobante " +
                            "HAVING SUM(df.cantidad * a.precioVenta) > :valorMinimo",
                    Object[].class
            );
            queryFacturasMayores.setParameter("valorMinimo", valorMinimo);
            List<Object[]> facturasMayores = queryFacturasMayores.getResultList();
            if (facturasMayores.isEmpty()) {
                System.out.println("No hay facturas con total mayor a $" + valorMinimo);
            } else {
                for (Object[] fila : facturasMayores) {
                    Double total = ((Number) fila[1]).doubleValue();  // ✅ conversión segura
                    System.out.printf("Factura N°: %s | Total: $%.2f%n", fila[0], total);
                }
            }

            // EJERCICIO 11. Consultar las facturas que contienen un Artículo específico, filtrando por el nombre del artículo
            System.out.println("\n=== FACTURAS QUE CONTIENEN UN ARTÍCULO ESPECÍFICO ===");
            String nombreArticuloABuscar = "Manzana"; // Artículo a buscar
            String jpqlFacturasPorArticulo = "SELECT DISTINCT f FROM Factura f JOIN f.detallesFactura fd JOIN fd.articulo a WHERE a.denominacion = :nombreArticulo";
            Query queryFacturasPorArticulo = em.createQuery(jpqlFacturasPorArticulo);
            queryFacturasPorArticulo.setParameter("nombreArticulo", nombreArticuloABuscar);
            List<Factura> facturasPorArticulo = queryFacturasPorArticulo.getResultList();
            System.out.println("Facturas que contienen el Artículo '" + nombreArticuloABuscar + "':");
            for (Factura f : facturasPorArticulo) {
                System.out.println("Factura N°: " + f.getNroComprobante() +
                        " - Cliente: " + f.getCliente().getRazonSocial() +
                        " - Total: $" + f.getTotal());
            }

            // EJERCICIO 12. Listar los Artículos filtrando por código parcial
            System.out.println("\n=== ARTÍCULOS FILTRADOS POR CÓDIGO PARCIAL ===");
            String codigoParcial = "13"; // Fragmento a buscar
            String patronBusqueda = "%" + codigoParcial + "%";
            String jpqlArticulosPorCodigo = "SELECT a FROM Articulo a WHERE a.codigo LIKE :patron";
            Query queryArticulosPorCodigo = em.createQuery(jpqlArticulosPorCodigo);
            queryArticulosPorCodigo.setParameter("patron", patronBusqueda);
            List<org.example.Articulo> articulosPorCodigo = queryArticulosPorCodigo.getResultList();
            System.out.println("Artículos cuyo código contiene '" + codigoParcial + "':");
            for (org.example.Articulo a : articulosPorCodigo) {
                System.out.println("Artículo: " + a.getDenominacion() + " - Código: " + a.getCodigo());
            }

            // EJERCICIO 13. Listar todos los Artículos cuyo precio sea mayor que el promedio de los precios de todos los Artículos
            System.out.println("\n=== ARTÍCULOS CON PRECIO > PROMEDIO GLOBAL ===");
            String jpql13 =
                    "SELECT a FROM Articulo a " +
                            "WHERE a.precioVenta > (SELECT AVG(a2.precioVenta) FROM Articulo a2)";
            TypedQuery<Articulo> q13 = em.createQuery(jpql13, Articulo.class);
            List<Articulo> articulosSobrePromedio = q13.getResultList();
            if (articulosSobrePromedio.isEmpty()) {
                System.out.println("No hay artículos por encima del promedio.");
            } else {
                articulosSobrePromedio.forEach(a ->
                        System.out.println("Articulo: " + a.getDenominacion() + " | $ " + a.getPrecioVenta())
                );
            }

            // EJERCICIO 14 (PRIMER EJEMPLO)
            // La cláusula EXISTS se usa para comprobar si existe al menos una fila que cumpla una condición dentro de una subconsulta.
            // Devuelve true si la subconsulta encuentra al menos un registro.
            // Devuelve false si la subconsulta no encuentra ninguno.
            System.out.println("\n=== CLIENTES CON AL MENOS UNA FACTURA ===");
            String jpql14a =
                    "SELECT c FROM Cliente c " +
                            "WHERE EXISTS (" +
                            "   SELECT 1 FROM Factura f WHERE f.cliente = c" +
                            ")";
            TypedQuery<Cliente> q14a = em.createQuery(jpql14a, Cliente.class);
            List<Cliente> clientesConFacturas = q14a.getResultList();
            if (clientesConFacturas.isEmpty()) {
                System.out.println("Ningún cliente tiene facturas.");
            } else {
                clientesConFacturas.forEach(c ->
                                System.out.println("Cliente: " + c.getRazonSocial() + " | CUIT: " + c.getCuit())
                );
            }

            // EJERCICIO 14 (SEGUNDO EJEMPLO)
            System.out.println("\n=== ARTÍCULOS VENDIDOS AL MENOS UNA VEZ ===");
            String jpql14b =
                    "SELECT a FROM Articulo a " +
                            "WHERE EXISTS (" +
                            "   SELECT 1 FROM FacturaDetalle fd WHERE fd.articulo = a" +
                            ")";
            TypedQuery<Articulo> q14b = em.createQuery(jpql14b, Articulo.class);
            List<Articulo> articulosVendidos = q14b.getResultList();
            if (articulosVendidos.isEmpty()) {
                System.out.println("No se registran artículos vendidos.");
            } else {
                articulosVendidos.forEach(a ->
                                System.out.println("Vendido: " + a.getDenominacion() + " | $ " + a.getPrecioVenta())
                );
            }




            // Cerrar el EntityManager y el EntityManagerFactory
            em.close();
            emf.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


