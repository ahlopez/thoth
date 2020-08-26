package com.f.thoth.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.f.thoth.backend.data.OrderState;
import com.f.thoth.backend.data.Role;
import com.f.thoth.backend.data.entity.Customer;
import com.f.thoth.backend.data.entity.HistoryItem;
import com.f.thoth.backend.data.entity.Order;
import com.f.thoth.backend.data.entity.OrderItem;
import com.f.thoth.backend.data.entity.PickupLocation;
import com.f.thoth.backend.data.entity.Product;
import com.f.thoth.backend.data.entity.User;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.ThothSession;
import com.f.thoth.backend.repositories.ObjectToProtectRepository;
import com.f.thoth.backend.repositories.OrderRepository;
import com.f.thoth.backend.repositories.PickupLocationRepository;
import com.f.thoth.backend.repositories.ProductRepository;
import com.f.thoth.backend.repositories.RoleRepository;
import com.f.thoth.backend.repositories.TenantRepository;
import com.f.thoth.backend.repositories.UserRepository;
import com.f.thoth.backend.service.TenantService;
import com.f.thoth.ui.utils.Constant;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
public class DataGenerator implements HasLogger {

   private static final String[] FILLING = new String[] { "Strawberry", "Chocolate", "Blueberry", "Raspberry",
   "Vanilla" };
   private static final String[] TYPE = new String[] { "Cake", "Pastry", "Tart", "Muffin", "Biscuit", "Bread", "Bagel",
         "Bun", "Brownie", "Cookie", "Cracker", "Cheese Cake" };
   private static final String[] FIRST_NAME = new String[] { "Ori", "Amanda", "Octavia", "Laurel", "Lael", "Delilah",
         "Jason", "Skyler", "Arsenio", "Haley", "Lionel", "Sylvia", "Jessica", "Lester", "Ferdinand", "Elaine",
         "Griffin", "Kerry", "Dominique" };
   private static final String[] LAST_NAME = new String[] { "Carter", "Castro", "Rich", "Irwin", "Moore", "Hendricks",
         "Huber", "Patton", "Wilkinson", "Thornton", "Nunez", "Macias", "Gallegos", "Blevins", "Mejia", "Pickett",
         "Whitney", "Farmer", "Henry", "Chen", "Macias", "Rowland", "Pierce", "Cortez", "Noble", "Howard", "Nixon",
         "Mcbride", "Leblanc", "Russell", "Carver", "Benton", "Maldonado", "Lyons" };

   private final Random random = new Random(1L);

   private TenantService             tenantService;
   private TenantRepository          tenantRepository;
   private RoleRepository            roleRepository;
   private OrderRepository           orderRepository;
   private UserRepository            userRepository;
   private ProductRepository         productRepository;
   private ObjectToProtectRepository objectToProtectRepository;
   private PickupLocationRepository  pickupLocationRepository;
   private PasswordEncoder           passwordEncoder;

   private int itemSequence = 0;

   @Autowired
   public DataGenerator(TenantService tenantService, OrderRepository orderRepository, UserRepository userRepository,
         ProductRepository productRepository, PickupLocationRepository pickupLocationRepository,
         TenantRepository tenantRepository, RoleRepository roleRepository, ObjectToProtectRepository objectToProtectRepository,
         PasswordEncoder passwordEncoder)
   {
      this.tenantService             = tenantService;
      this.orderRepository           = orderRepository;
      this.userRepository            = userRepository;
      this.productRepository         = productRepository;
      this.pickupLocationRepository  = pickupLocationRepository;
      this.tenantRepository          = tenantRepository;
      this.roleRepository            = roleRepository;
      this.objectToProtectRepository = objectToProtectRepository;
      this.passwordEncoder           = passwordEncoder;

   }//DataGenerator

   @SuppressWarnings("unused")
   @PostConstruct
   public void loadData() {
      if (userRepository.count() != 0L) {
         getLogger().info("Using existing database");
         return;
      }

      getLogger().info("Generating demo data");

      getLogger().info("... generating tenants");
      ThothSession session = new ThothSession(tenantService);
      Tenant tenant1 = createTenant(tenantRepository, "FCONSULTORES");
      ThothSession.setTenant(tenant1);

      Tenant tenant2 = createTenant(tenantRepository,"SEI");
      getLogger().info("... generating roles");
      com.f.thoth.backend.data.security.Role role1 = createRole(tenant1, "Gerente");
      com.f.thoth.backend.data.security.Role role2 = createRole(tenant1, "Admin");
      com.f.thoth.backend.data.security.Role role3 = createRole(tenant1, "Supervisor");
      com.f.thoth.backend.data.security.Role role4 = createRole(tenant1, "Operador");
      com.f.thoth.backend.data.security.Role role5 = createRole(tenant1, "Publico");

      tenant1.addRole(role1);
      tenant1.addRole(role2);
      tenant1.addRole(role3);
      tenant1.addRole(role4);
      tenant1.addRole(role5);

      com.f.thoth.backend.data.security.Role role6 = createRole(tenant2, "CEO");
      com.f.thoth.backend.data.security.Role role7 = createRole(tenant2, "Admin2");
      com.f.thoth.backend.data.security.Role role8 = createRole(tenant2, "CFO");
      com.f.thoth.backend.data.security.Role role9 = createRole(tenant2, "CIO");
      com.f.thoth.backend.data.security.Role role10 = createRole(tenant2, "COO");

      tenant2.addRole(role6);
      tenant2.addRole(role7);
      tenant2.addRole(role8);
      tenant2.addRole(role9);
      tenant2.addRole(role10);

      getLogger().info("... generating Objects to protect" );
      ObjectToProtect obj01 = createObject( tenant1, Constant.TITLE_CLIENTES                                , null );  // Clientes
      ObjectToProtect obj02 = createObject( tenant1,    Constant.TITLE_TENANTS                              , obj01);  // Fondo
      ObjectToProtect obj03 = createObject( tenant1, Constant.TITLE_SEGURIDAD                               , null );  // Seguridad
      ObjectToProtect obj04 = createObject( tenant1,    Constant.TITLE_OBJETOS                              , obj03);  // Operaciones
      ObjectToProtect obj05 = createObject( tenant1,    Constant.TITLE_INFORMACION                          , obj03);  // Informacion
      ObjectToProtect obj06 = createObject( tenant1,    Constant.TITLE_ROLES                                , obj03);  // Roles
      ObjectToProtect obj07 = createObject( tenant1,    Constant.TITLE_PERMISOS_EJECUCION                   , obj03);  // Permisos de ejecucion
      ObjectToProtect obj08 = createObject( tenant1,    Constant.TITLE_PERMISOS_ACCESO                      , obj03);  // Permisos de acceso
      ObjectToProtect obj09 = createObject( tenant1, Constant.TITLE_ADMINISTRACION                          , null );  // Administracion
      ObjectToProtect obj10 = createObject( tenant1,    Constant.TITLE_PARAMETROS                           , obj09);  // Parametros
      ObjectToProtect obj11 = createObject( tenant1,    Constant.TITLE_USUARIOS                             , obj09);  // Usuarios
      ObjectToProtect obj12 = createObject( tenant1,    Constant.TITLE_GRUPOS_USUARIOS                      , obj09);  // Grupos de usuarios
      ObjectToProtect obj13 = createObject( tenant1, Constant.TITLE_CLASIFICACION                           , null );  // Clasificacion
      ObjectToProtect obj14 = createObject( tenant1,    Constant.TITLE_FONDOS                               , obj13);  // Fondos
      ObjectToProtect obj15 = createObject( tenant1,    Constant.TITLE_OFICINAS                             , obj13);  // Oficinas
      ObjectToProtect obj16 = createObject( tenant1,    Constant.TITLE_SERIES                               , obj13);  // Series
      ObjectToProtect obj17 = createObject( tenant1,    Constant.TITLE_SUBSERIES                            , obj13);  // Subseries
      ObjectToProtect obj18 = createObject( tenant1,    Constant.TITLE_TIPOS_DOCUMENTALES                   , obj13);  // Tipos documentales
      ObjectToProtect obj19 = createObject( tenant1, Constant.TITLE_ADMIN_EXPEDIENTES                       , null );  // Gestion expedientes
      ObjectToProtect obj20 = createObject( tenant1,    Constant.TITLE_EXPEDIENTES                          , obj19);  // Expedientes mayores
      ObjectToProtect obj21 = createObject( tenant1,    Constant.TITLE_SUBEXPEDIENTES                       , obj19);  // Sub-expedientes
      ObjectToProtect obj22 = createObject( tenant1,    Constant.TITLE_VOLUMENES                            , obj19);  // Volumenes
      ObjectToProtect obj23 = createObject( tenant1,    Constant.TITLE_INDICE                               , obj19);  // Indice de expedientes
      ObjectToProtect obj24 = createObject( tenant1,    Constant.TITLE_EXPORTACION                          , obj19);  // Exportacion de expedientes
      ObjectToProtect obj25 = createObject( tenant1,    Constant.TITLE_IMPORTACION                          , obj19);  // Importacion de expedientes
      ObjectToProtect obj26 = createObject( tenant1,    Constant.TITLE_COPIA_DOCUMENTOS                     , obj19);  // Copia documento a otro expediente
      ObjectToProtect obj27 = createObject( tenant1,    Constant.TITLE_TRANSER_DOCUMENTOS                   , obj19);  // Transferencia de documento a otro expediente
      ObjectToProtect obj28 = createObject( tenant1, Constant.TITLE_TRAMITE                                 , null );  // Tramite de documentos
      ObjectToProtect obj29 = createObject( tenant1,    Constant.TITLE_BANDEJA                              , obj28);  // Bandeja personal
      ObjectToProtect obj30 = createObject( tenant1,    Constant.TITLE_CLASIFICACION_DOCUMENTOS             , obj28);  // Clasificacion de documento
      ObjectToProtect obj31 = createObject( tenant1,    Constant.TITLE_RETORNO                              , obj28);  // Devolucion de documento
      ObjectToProtect obj32 = createObject( tenant1,    Constant.TITLE_RE_ENVIO                             , obj28);  // Re-envio de documento
      ObjectToProtect obj33 = createObject( tenant1,    Constant.TITLE_BORRADORES                           , obj28);  // Carga borrador de documento
      ObjectToProtect obj34 = createObject( tenant1,    Constant.TITLE_FIRMA                                , obj28);  // Firma de documento
      ObjectToProtect obj35 = createObject( tenant1,    Constant.TITLE_ENVIO                                , obj28);  // Ordena envio de documento
      ObjectToProtect obj36 = createObject( tenant1, Constant.TITLE_RECEPCION                               , null );  // Recepcion de documentos
      ObjectToProtect obj37 = createObject( tenant1,    Constant.TITLE_RECEPCION_DOCUMENTOS                 , obj36);  // Recepcion en ventanilla
      ObjectToProtect obj38 = createObject( tenant1,    Constant.TITLE_RECEPCION_E_MAIL                     , obj36);  // Recepcion correo electronico
      ObjectToProtect obj39 = createObject( tenant1,    Constant.TITLE_DIGITALIZACION                       , obj36);  // Digitalizacion
      ObjectToProtect obj40 = createObject( tenant1,    Constant.TITLE_DIRECCIONAMIENTO                     , obj36);  // Enrutamiento de documentos
      ObjectToProtect obj41 = createObject( tenant1, Constant.TITLE_CORRESPONDENCIA_EXTERNA                 , null );  // Envio de documentos
      ObjectToProtect obj42 = createObject( tenant1,    Constant.TITLE_REGISTRO_ENVIOS                      , obj41);  // Consolidacion envoos externos
      ObjectToProtect obj43 = createObject( tenant1,    Constant.TITLE_ENVIO_EXTERNO                        , obj41);  // Envia correspondencia externa
      ObjectToProtect obj44 = createObject( tenant1,    Constant.TITLE_CONFIRMACION_ENVIO                   , obj41);  // Confirmacion de recepcion
      ObjectToProtect obj45 = createObject( tenant1, Constant.TITLE_CONSULTA                                , null );  // Consulta
      ObjectToProtect obj46 = createObject( tenant1,    Constant.TITLE_DOCUMENTOS                           , obj45);  // Consulta de documentos
      ObjectToProtect obj47 = createObject( tenant1,       Constant.TITLE_CONSULTA_LIBRE                    , obj46);  // Consulta libre documentos
      ObjectToProtect obj48 = createObject( tenant1,       Constant.TITLE_CONSULTA_METADATOS                , obj46);  // Consulta documentos segun metadatos
      ObjectToProtect obj49 = createObject( tenant1,    Constant.TITLE_CONSULTA_EXPEDIENTES                 , obj45);  // Consulta de expedientes
      ObjectToProtect obj50 = createObject( tenant1,       Constant.TITLE_CONSULTA_EXPEDIENTES_LIBRE        , obj49);  // Consulta de expedientes segun texto libre
      ObjectToProtect obj51 = createObject( tenant1,       Constant.TITLE_CONSULTA_EXPEDIENTES_METADATOS    , obj49);  // Consulta de expedientes segun metadatos
      ObjectToProtect obj52 = createObject( tenant1,       Constant.TITLE_CONSULTA_EXPEDIENTES_CLASIFICACION, obj49);  // Consulta de expedientes segun clasificacion
      ObjectToProtect obj53 = createObject( tenant1, Constant.TITLE_PROCESOS                                , null );  // Procesos
      ObjectToProtect obj54 = createObject( tenant1,    Constant.TITLE_EJECUCION_PROCESO                    , obj53);  // Ejecucion de proceso
      ObjectToProtect obj55 = createObject( tenant1,    Constant.TITLE_DEFINICION_PROCESO                   , obj53);  // Definicion de proceso
      ObjectToProtect obj56 = createObject( tenant1, Constant.TITLE_ARCHIVO                                 , null );  // Archivo
      ObjectToProtect obj57 = createObject( tenant1,    Constant.TITLE_LOCALES                              , obj56);  // Locales
      ObjectToProtect obj58 = createObject( tenant1,    Constant.TITLE_TRANSFERENCIA                        , obj56);  // Preparacion de transferencia
      ObjectToProtect obj59 = createObject( tenant1,    Constant.TITLE_RECIBO_TRANSFERENCIA                 , obj56);  // Recepcion de transferencia
      ObjectToProtect obj60 = createObject( tenant1,    Constant.TITLE_LOCALIZACION                         , obj56);  // Localizacion de documentos
      ObjectToProtect obj61 = createObject( tenant1,    Constant.TITLE_PRESTAMO                             , obj56);  // Prestamos
      ObjectToProtect obj62 = createObject( tenant1,       Constant.TITLE_PRESTAMO_EXPEDIENTE               , obj61);  // Prestamo de expedientes
      ObjectToProtect obj63 = createObject( tenant1,       Constant.TITLE_DEVOLUCION                        , obj61);  // Retorno de expediente
      ObjectToProtect obj64 = createObject( tenant1,    Constant.TITLE_INDICES_ARCHIVO                      , obj56);  // Indice de archivo

      getLogger().info("... generating users");
      User baker = createBaker(userRepository, passwordEncoder);
      User barista = createBarista(userRepository, passwordEncoder);
      createAdmin(userRepository, passwordEncoder);
      // A set of products without constrains that can be deleted
      createDeletableUsers(userRepository, passwordEncoder);

      getLogger().info("... generating products");
      // A set of products that will be used for creating orders.
      Supplier<Product> productSupplier = createProducts(productRepository, 8);
      // A set of products without relationships that can be deleted
      createProducts(productRepository, 4);

      getLogger().info("... generating pickup locations");
      Supplier<PickupLocation> pickupLocationSupplier = createPickupLocations(pickupLocationRepository);

      getLogger().info("... generating orders");
      createOrders(orderRepository, productSupplier, pickupLocationSupplier, barista, baker);

      getLogger().info("Generated demo data");

   }//loadData

   private ObjectToProtect createObject( Tenant tenant, String name, ObjectToProtect owner)
   {
      ObjectToProtect obj = new ObjectToProtect( name, owner);
      obj.setTenant(tenant);
      ObjectToProtect savedObject = objectToProtectRepository.saveAndFlush(obj);
      return savedObject;
   }//createObject

   private Tenant createTenant(TenantRepository tenantRepository, String name)
   {
      Tenant tenant = new Tenant();
      tenant.setName(name);
      tenant.setLocked(false);
      tenant.setAdministrator("admin@vaadin.com");
      LocalDate now = LocalDate.now();
      tenant.setFromDate( now.minusMonths(random.nextInt(36)));
      tenant.setToDate(now.plusYears(random.nextInt(10)));
      tenantRepository.save(tenant);

      return tenant;
   }//createTenant

   private com.f.thoth.backend.data.security.Role createRole( Tenant tenant, String name)
   {
      com.f.thoth.backend.data.security.Role  role = new com.f.thoth.backend.data.security.Role();
      role.setTenant(tenant);
      role.setName(name);
      roleRepository.saveAndFlush(role);
      tenant.addRole(role);

      return role;
   }//createRole

   private void fillCustomer(Customer customer)
   {
      String first = getRandom(FIRST_NAME);
      String last = getRandom(LAST_NAME);
      customer.setFullName(first + " " + last);
      customer.setCode( ""+ (++itemSequence));
      customer.setPhoneNumber(getRandomPhone());
      if (random.nextInt(10) == 0) {
         customer.setDetails("Very important customer");
      }
   }

   private String getRandomPhone() {
      return "+1-555-" + String.format("%04d", random.nextInt(10000));
   }

   private void createOrders(OrderRepository orderRepo, Supplier<Product> productSupplier,
         Supplier<PickupLocation> pickupLocationSupplier, User barista, User baker)
   {
      int yearsToInclude = 2;
      LocalDate now = LocalDate.now();
      LocalDate oldestDate = LocalDate.of(now.getYear() - yearsToInclude, 1, 1);
      LocalDate newestDate = now.plusMonths(1L);

      // Create first today's order
      Order order = createOrder(productSupplier, pickupLocationSupplier, barista, baker, now);
      order.setDueTime(LocalTime.of(8, 0));
      order.setHistory(order.getHistory().subList(0, 1));
      order.setItems(order.getItems().subList(0, 1));
      orderRepo.save(order);

      for (LocalDate dueDate = oldestDate; dueDate.isBefore(newestDate); dueDate = dueDate.plusDays(1))
      {
         // Create a slightly upwards trend - everybody wants to be
         // successful
         int relativeYear = dueDate.getYear() - now.getYear() + yearsToInclude;
         int relativeMonth = relativeYear * 12 + dueDate.getMonthValue();
         double multiplier = 1.0 + 0.03 * relativeMonth;
         int ordersThisDay = (int) (random.nextInt(10) + 1 * multiplier);
         for (int i = 0; i < ordersThisDay; i++) {
            orderRepo.save(createOrder(productSupplier, pickupLocationSupplier, barista, baker, dueDate));
         }
      }// for dueDate...
   }//createOrders

   private Order createOrder(Supplier<Product> productSupplier, Supplier<PickupLocation> pickupLocationSupplier,
         User barista, User baker, LocalDate dueDate)
   {
      Order order = new Order(barista);

      fillCustomer(order.getCustomer());
      order.setPickupLocation(pickupLocationSupplier.get());
      order.setDueDate(dueDate);
      order.setCode( ""+ (++itemSequence));
      order.setDueTime(getRandomDueTime());
      order.changeState(barista, getRandomState(order.getDueDate()));

      int itemCount = random.nextInt(3);
      List<OrderItem> items = new ArrayList<>();
      for (int i = 0; i <= itemCount; i++) {
         OrderItem item = new OrderItem();
         item.setCode(""+ (++itemSequence));
         Product product;
         do {
            product = productSupplier.get();
         } while (containsProduct(items, product));
         item.setProduct(product);
         item.setQuantity(random.nextInt(10) + 1);
         if (random.nextInt(5) == 0) {
            if (random.nextBoolean()) {
               item.setComment("Lactose free");
            } else {
               item.setComment("Gluten free");
            }
         }
         items.add(item);
      }
      order.setItems(items);

      order.setHistory(createOrderHistory(order, barista, baker));

      return order;
   }//createOrder

   private List<HistoryItem> createOrderHistory(Order order, User barista, User baker)
   {
      ArrayList<HistoryItem> history = new ArrayList<>();
      HistoryItem item = new HistoryItem(barista, "Order placed");
      item.setNewState(OrderState.NEW);
      LocalDateTime orderPlaced = order.getDueDate().minusDays(random.nextInt(5) + 2L).atTime(random.nextInt(10) + 7, 00);
      item.setTimestamp(orderPlaced);
      history.add(item);
      if (order.getState() == OrderState.CANCELLED) {
         item = new HistoryItem(barista, "Order cancelled");
         item.setNewState(OrderState.CANCELLED);
         item.setTimestamp(orderPlaced.plusDays(random
               .nextInt((int) orderPlaced.until(order.getDueDate().atTime(order.getDueTime()), ChronoUnit.DAYS))));
         history.add(item);
      } else if (order.getState() == OrderState.CONFIRMED || order.getState() == OrderState.DELIVERED
            || order.getState() == OrderState.PROBLEM || order.getState() == OrderState.READY) {
         item = new HistoryItem(baker, "Order confirmed");
         item.setNewState(OrderState.CONFIRMED);
         item.setTimestamp(orderPlaced.plusDays(random.nextInt(2)).plusHours(random.nextInt(5)));
         history.add(item);

         if (order.getState() == OrderState.PROBLEM) {
            item = new HistoryItem(baker, "Can't make it. Did not get any ingredients this morning");
            item.setNewState(OrderState.PROBLEM);
            item.setTimestamp(order.getDueDate().atTime(random.nextInt(4) + 4, 0));
            history.add(item);
         } else if (order.getState() == OrderState.READY || order.getState() == OrderState.DELIVERED) {
            item = new HistoryItem(baker, "Order ready for pickup");
            item.setNewState(OrderState.READY);
            item.setTimestamp(order.getDueDate().atTime(random.nextInt(2) + 8, random.nextBoolean() ? 0 : 30));
            history.add(item);
            if (order.getState() == OrderState.DELIVERED) {
               item = new HistoryItem(baker, "Order delivered");
               item.setNewState(OrderState.DELIVERED);
               item.setTimestamp(order.getDueDate().atTime(order.getDueTime().minusMinutes(random.nextInt(120))));
               history.add(item);
            }
         }
      }

      return history;
   }//createOrderHistory

   private boolean containsProduct(List<OrderItem> items, Product product)
   {
      for (OrderItem item : items) {
         if (item.getProduct() == product) {
            return true;
         }
      }
      return false;
   }//containsProduct

   private LocalTime getRandomDueTime()
   {
      int time = 8 + 4 * random.nextInt(3);
      return LocalTime.of(time, 0);
   }//getRandomDueTime

   private OrderState getRandomState(LocalDate due)
   {
      LocalDate today = LocalDate.now();
      LocalDate tomorrow = today.plusDays(1);
      LocalDate twoDays = today.plusDays(2);

      if (due.isBefore(today)) {
         if (random.nextDouble() < 0.9) {
            return OrderState.DELIVERED;
         } else {
            return OrderState.CANCELLED;
         }
      } else {
         if (due.isAfter(twoDays)) {
            return OrderState.NEW;
         } else if (due.isAfter(tomorrow)) {
            // in 1-2 days
            double resolution = random.nextDouble();
            if (resolution < 0.8) {
               return OrderState.NEW;
            } else if (resolution < 0.9) {
               return OrderState.PROBLEM;
            } else {
               return OrderState.CANCELLED;
            }
         } else {
            double resolution = random.nextDouble();
            if (resolution < 0.6) {
               return OrderState.READY;
            } else if (resolution < 0.8) {
               return OrderState.DELIVERED;
            } else if (resolution < 0.9) {
               return OrderState.PROBLEM;
            } else {
               return OrderState.CANCELLED;
            }
         }

      }
   }//getRandomState

   private <T> T getRandom(T[] array)
   {
      return array[random.nextInt(array.length)];
   }

   private Supplier<PickupLocation> createPickupLocations(PickupLocationRepository pickupLocationRepository)
   {
      List<PickupLocation> pickupLocations = Arrays.asList(
            pickupLocationRepository.save(createPickupLocation("Store")),
            pickupLocationRepository.save(createPickupLocation("Bakery")));
      return () -> pickupLocations.get(random.nextInt(pickupLocations.size()));
   }//createPickupLocations

   private PickupLocation createPickupLocation(String name)
   {
      PickupLocation store = new PickupLocation();
      store.setName(name);
      store.setCode( name);
      return store;
   }//createPickupLocation

   private Supplier<Product> createProducts(ProductRepository productsRepo, int numberOfItems)
   {
      List<Product> products  = new ArrayList<>();
      for (int i = 0; i < numberOfItems; i++) {
         Product product = new Product();
         product.setName(getRandomProductName()+i);
         product.setCode(product.getName());
         double doublePrice = 2.0 + random.nextDouble() * 100.0;
         product.setPrice((int) (doublePrice * 100.0));
         products.add(productsRepo.save(product));
      }
      return () -> {
         double cutoff = 2.5;
         double g = random.nextGaussian();
         g = Math.min(cutoff, g);
         g = Math.max(-cutoff, g);
         g += cutoff;
         g /= (cutoff * 2.0);
         return products.get((int) (g * (products.size() - 1)));
      };
   }//createProducts

   private String getRandomProductName()
   {
      String firstFilling = getRandom(FILLING);
      String name;
      if (random.nextBoolean()) {
         String secondFilling;
         do {
            secondFilling = getRandom(FILLING);
         } while (secondFilling.equals(firstFilling));

         name = firstFilling + " " + secondFilling;
      } else {
         name = firstFilling;
      }
      name += " " + getRandom(TYPE);

      return name;
   }//getRandomProductName

   private User createBaker(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      return userRepository.save(
            createUser("baker@vaadin.com", "Heidi", "Carter", passwordEncoder.encode("baker"), Role.BAKER, false));
   }//createBaker

   private User createBarista(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      return userRepository.save(createUser("barista@vaadin.com", "Malin", "Castro",
            passwordEncoder.encode("barista"), Role.BARISTA, true));
   }//createBarista

   private User createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      return userRepository.save(
            createUser("admin@vaadin.com", "Göran", "Rich", passwordEncoder.encode("admin"), Role.ADMIN, true));
   }//createAdmin

   private void createDeletableUsers(UserRepository userRepository, PasswordEncoder passwordEncoder)
   {
      userRepository.save(
            createUser("peter@vaadin.com", "Peter", "Bush", passwordEncoder.encode("peter"), Role.BARISTA, false));
      userRepository
      .save(createUser("mary@vaadin.com", "Mary", "Ocon", passwordEncoder.encode("mary"), Role.BAKER, true));
   }//createDeletableUsers

   private User createUser(String email, String firstName, String lastName, String passwordHash, String role, boolean locked)
   {
      User user = new User();
      user.setEmail(email);
      user.setCode(email);
      user.setFirstName(firstName);
      user.setLastName(lastName);
      user.setPasswordHash(passwordHash);
      user.setRole(role);
      user.setLocked(locked);
      return user;
   }//createUser

}//DataGenerator
