package com.company;

import com.company.CategoriesEntity;
import com.company.EmployeesEntity;
import com.company.OrdersEntity;
import org.hibernate.*;
import org.hibernate.query.Query;
import org.hibernate.cfg.Configuration;

import javax.persistence.metamodel.EntityType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Main {
    private static final SessionFactory ourSessionFactory;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            ourSessionFactory = configuration.buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }

    public static void main(final String[] args) {
        final Session session = getSession();
//ejecutarMainOriginal(session);
//ejemplo1(session); // esto es casi igual que el original, no usar
//ejecutarQuery(session);
//ejecutarGet(session);
//ejecutarLoad(session);
//ejecutarAVG(session);
//ejecutarTransacciones(session);
//ejecutarIterate(session);
//ejercicioEmpleados(session);
//ejecutarUniqueResult(session);
//ejecutarConsultasParametros(session);
//consultasVariasColumnas(session);
        ejemploJoinEmpleadosPedidos(session);
        if(session != null) {
            session.close();
        }

    }

    private static void ejecutarMainOriginal(Session session) {
        try {
            System.out.println("querying all the managed entities...");
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            for (EntityType<?> entityType : metamodel.getEntities()) {
                final String entityName = entityType.getName();
                final Query query = session.createQuery("from " + entityName);
                System.out.println("executing: " + query.getQueryString());
                for (Object o : query.list()) {
                    System.out.println(" " + o);
                }
            }
        } finally {
            session.close();
        }
    }

    private static void ejercicioEmpleados(Session session) {
// 1. Recuperar num empleados
        Query q = session.createQuery("SELECT count(*) FROM EmployeesEntity"); // HQL, usamos el nombre de la entidad no de la tabla
        long cantidad = (Long) q.getSingleResult();
// getSingle devuelve el primer resultado incluso si la consulta devuelve varios
        System.out.println("Numero de empleados: " + cantidad);

// 2. Imprimir empleados
        for(int i=1; i<=cantidad; i++) {
            EmployeesEntity emp = session.get(EmployeesEntity.class, i);
// Habria que comprobar si es null, no lo hago
            System.out.println(emp);
        }

    }

    public static void ejemplo1(Session session) {
        try {
            System.out.println("querying all the managed entities...");
            final Metamodel metamodel = session.getSessionFactory().getMetamodel();
            for (EntityType<?> entityType : metamodel.getEntities()) {
                final String entityName = entityType.getName();
                final Query query = session.createQuery("from " + entityName);
                System.out.println("executing: " + query.getQueryString());
                for (Object o : query.list()) {
                    System.out.println(" " + o);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void ejecutarQuery(Session session) {
        Query q = session.createQuery("from CategoriesEntity"); // HQL, usamos el nombre de la entidad no de la tabla
        List<CategoriesEntity> lista = q.list();
// Obtenemos un Iterador y recorremos la lista.
        Iterator<CategoriesEntity> iter = lista.iterator();
        System.out.println("Número de registros: " + lista.size());
        while (iter.hasNext())
        {
            CategoriesEntity cat = (CategoriesEntity) iter.next();
            System.out.println("***************");
            System.out.println("CategoryName: " + cat.getCategoryName());
            System.out.println("CategoryId: " + cat.getCategoryId());
            System.out.println("CategoryDescription: " + cat.getDescription());
        }
    }

    private static void ejecutarGet(Session session) {
        EmployeesEntity e1 = session.get(EmployeesEntity.class, 1); // Que pasa si el ID no existe
        if(e1 != null) {
            System.out.println("FirstName: " + e1.getFirstName());
            System.out.println("LastName: " + e1.getLastName());
            System.out.println("City: " + e1.getCity());
            System.out.println("Country: " + e1.getCountry());
            System.out.println("Birthdate: " + e1.getBirthDate());
        }
    }

    private static void ejecutarLoad(Session session) {
        EmployeesEntity emp = session.load(EmployeesEntity.class, 1); // Excepcion si no existe
        System.out.println(emp);

        emp = session.get(EmployeesEntity.class, 9); // null si no existe
        if(emp != null) {
            System.out.println(emp);
        } else {
            System.out.println("No existe empleado con ese ID");
        }
    }

    private static double ejecutarAVG(Session session) {
        Query q = session.createQuery("SELECT AVG(unitPrice) FROM ProductsEntity "); // HQL, usamos el nombre de la entidad no de la tabla
        System.out.println(q.getSingleResult());
// getSingle devuelve el primer resultado incluso si la consulta devuelve varios

        return (Double) q.getSingleResult();
    }

    private static void ejecutarTransacciones(Session session) {
// Si queremos modificar la base de datos debemos comenzar una transacción
        Transaction tx = session.beginTransaction();
        EmployeesEntity emp = new EmployeesEntity(); // Estado transitorio
        emp.setFirstName("Alvaro");
        emp.setLastName("Gamez");
        emp.setEmployeeId(117);
// Parar aqui y comprobar que en la base de datos no existe el nuevo empleado
        System.out.println("Haciendo persistente a Jose Gamez");
        session.save(emp); // Persistente, a partir de ahora estará en la bbdd y cualquier modificación sobre el objeto se vera reflejada (cuando se haga commit)
        emp.setFirstName("Pepe"); // Esto se verá reflejado
// Parar aqui y comprobar que en la base de datos no existe el nuevo empleado

        tx.commit(); // Si no hacemos commit, no se guardan nunca los cambios
//System.out.println("Commit hecho");

// ¿Como podemos añadir un constructor para no tener que usar los set?
    }

    private static void ejecutarIterate(Session session) {
        Query q = session.createQuery("from CategoriesEntity");
// Le dice al driver JDBC que devuelva datos en grupos de 3, cuando se necesiten mas de nuevo 3, y asi ->
// optimizacion de consultas que devuelven muchas filas
        q.setFetchSize(3);
        Iterator iter = q.iterate();
        while (iter.hasNext())
        {
//extraer el objeto
            CategoriesEntity cat = (CategoriesEntity) iter.next();
            System.out.println("Category info: " + cat.getCategoryId() + "," + cat.getCategoryName());
        }
        session.close();
    }

    private static void ejecutarUniqueResult(Session session) {

// Cuando sabemos que un query devuelve un solo resultado, podemos usar unique result y hacer
// un casting a la clase esperada.

        String hql = "from CategoriesEntity as cat where cat.categoryId = 1";
        Query q = session.createQuery(hql);
        CategoriesEntity cat = (CategoriesEntity) q.uniqueResult();
        System.out.println("Category: " + cat.getCategoryId() + "," + cat.getCategoryName());

        hql = "select avg(cat.categoryId) from CategoriesEntity as cat";
        Query cons = session.createQuery(hql);
        Double suma = (Double) cons.uniqueResult();
        System.out.println("Average: " + suma);
        session.close();
    }

    private static void ejecutarConsultasParametros(Session session) {
        String hql = "from EmployeesEntity where employeeId = :numemple";
        Query q = session.createQuery(hql);
        q.setParameter("numemple", 1);
        EmployeesEntity emple = (EmployeesEntity) q.uniqueResult();
        System.out.println("Employee: " + emple.getEmployeeId() + "," + emple.getLastName());
        System.out.println("");

// Empleados cuyo número de departamento sea 10 y el oficio DIRECTOR

        Query q2 = session.createQuery("from EmployeesEntity where employeeId > :numemple and title = :ofi");
        q2.setParameter("numemple", 1);
        q2.setParameter("ofi", "Sales Representative");
        List<EmployeesEntity> lista = q2.list();
        Iterator<EmployeesEntity> iter = lista.iterator();
        while (iter.hasNext()) {
            EmployeesEntity emp = (EmployeesEntity) iter.next();
            System.out.println("Employee: " + emp.getEmployeeId() + "," + emp.getLastName() + "," + emp.getTitle());
        }
        System.out.println("");

        List<Integer> numeros = new ArrayList<Integer>();
        numeros.add(1);
        numeros.add(3);
        numeros.add(5);
        String hql5 = "from EmployeesEntity emp where emp.employeeId in (:lista)";
        Query q5 = session.createQuery(hql5);
        q5.setParameterList("lista", numeros);
        List<EmployeesEntity> lista4 = q5.list();
        Iterator<EmployeesEntity> iter4 = lista4.iterator();
        while (iter4.hasNext()) {
            EmployeesEntity emp = (EmployeesEntity) iter4.next();
            System.out.println("Employee: " + emp.getEmployeeId() + "," + emp.getLastName());
        }

    }

    private static void consultasVariasColumnas(Session session) {

// 1 Mediante hibernate, crear una lista con todos los pedidos atendidos
//por todos un empleado con ID par. Imprimir la lista
//(debe reimplementar el método toString)

        String sql = "FROM OrdersEntity WHERE MOD(employeeId,2)=0";
        Query q = session.createQuery(sql); // HQL, usamos el nombre de la entidad no de la tabla
        List<OrdersEntity> pedidos = q.list();
        for (OrdersEntity ord: pedidos) {
            System.out.println(ord);
        }
        System.out.println("Total de resultados: " + pedidos.size());

// 2 Mediante hibernate, contar todos los productos de un pedido en particular
//(elija el que desee). La consulta debe devolver un único valor
//(el número) que es lo que se imprimirá por pantalla.

        String hql = "select count(*) from OrderDetailsEntity where orderId = 10248";
        q = session.createQuery(hql);
        long value = (Long) q.uniqueResult();
        System.out.println("Cantidad de productos: " + value);

// 3 Mostrar el numero de pedidos por cada pais y ciudad de envio.

        System.out.println("********************************");
        System.out.println("EJEMPLO MULTI COLUMNA CON ARRAYS");
        hql = "select shipCountry, shipCity, count(*) from OrdersEntity group by shipCity, shipCountry";
        q = session.createQuery(hql);
        List<Object[]> lista = q.list();
        System.out.println("Numero de filas: " + lista.size());
        for(int i=0; i< lista.size(); i++) {
            Object[] fila = lista.get(i);
            System.out.println(fila[0] + "," + fila[1] + "," + fila[2]);
        }

// 4 Repetimos el anterior pero de una forma distinta

        System.out.println("********************************");
        System.out.println("EJEMPLO MULTI COLUMNA CON LISTAS");
        hql = "select new List(shipCountry, shipCity, count(*)) from OrdersEntity group by shipCity, shipCountry";
        q = session.createQuery(hql);
        List<List<Object>> listaBis = q.list();
        System.out.println("Numero de filas: " + listaBis.size());
        for(int i=0; i< listaBis.size(); i++) {
            List<Object> fila = listaBis.get(i);
            System.out.println(fila.get(0) + "," + fila.get(1) + "," + fila.get(2));
        }

    }

    private static void ejemploJoinEmpleadosPedidos(Session session) {
        String sql = "FROM EmployeesEntity";
        Query q = session.createQuery(sql); // HQL, usamos el nombre de la entidad no de la tabla
        List<EmployeesEntity> listaEmpleados = q.list();
        System.out.println("Numero de empleados: " + listaEmpleados.size());
        System.out.println("****************");
        System.out.println("Empleado cero:");
        System.out.println("****************");
        EmployeesEntity emp0 = listaEmpleados.get(0);
        System.out.println(emp0);
        System.out.println();
        Collection pedidosEmp0 = emp0.getOrdersByEmployeeId();
        Iterator it = pedidosEmp0.iterator();
        System.out.println("*******");
        System.out.println("Pedidos");
        System.out.println("*******");
        while(it.hasNext()) {
            OrdersEntity pedido = (OrdersEntity) it.next();
            System.out.println(pedido);
        }
    }

}