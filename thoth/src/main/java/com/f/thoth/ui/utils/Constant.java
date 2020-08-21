package com.f.thoth.ui.utils;

import java.util.Locale;

import org.springframework.data.domain.Sort;

public class Constant
{

   public static final Locale APP_LOCALE = Locale.US;
   public static final String TENANT= "TENANT";
   public static final Integer MIN_CATEGORY = new Integer(0);
   public static final Integer MAX_CATEGORY = new Integer(5);
   public static final Integer DEFAULT_CATEGORY = MIN_CATEGORY;

   public static final String PAGE_ROOT = "";
   public static final String PAGE_STOREFRONT = "storefront";
   public static final String PAGE_STOREFRONT_EDIT = "storefront/edit";
   public static final String PAGE_DASHBOARD = "dashboard";
   public static final String PAGE_USERS = "users";
   public static final String PAGE_USER_GROUPS = "user_groups";
   public static final String PAGE_SINGLE_USERS = "single_users";
   public static final String PAGE_PRODUCTS = "products";
   public static final String PAGE_OBJECT_TO_PROTECT = "objects";

   public static final String TITLE_STOREFRONT = "Storefront";
   public static final String TITLE_DASHBOARD = "Dashboard";

   public static final String TITLE_ADMINISTRATION = "Administración";
   public static final String TITLE_USERS = "Usuarios";
   public static final String TITLE_USER_GROUPS = "Grupos de Usuarios";
   public static final String TITLE_SINGLE_USERS = "Usuarios individuales";
   public static final String TITLE_PRODUCTS = "Products";
   public static final String TITLE_LOGOUT = "Terminar";
   public static final String TITLE_NOT_FOUND = "No encontró la página";
   public static final String TITLE_ACCESS_DENIED = "Acceso denegado";
   public static final String TITLE_OBJECT_TO_PROTECT = "Protección";


  public static final String  PAGE_CLIENTES                                 = "cliente";
  public static final String     PAGE_TENANTS                               = "tenants";
  public static final String  PAGE_SEGURIDAD                                = "seguridad";
  public static final String     PAGE_OBJETOS                               = "objetos_a_proteger";
  public static final String     PAGE_INFORMACION                           = "informacion_a_proteger";
  public static final String     PAGE_ROLES                                 = "roles";
  public static final String     PAGE_PERMISOS_EJECUCION                    = "permisos_ejecucion";
  public static final String     PAGE_PERMISOS_ACCESO                       = "permisos_acceso";
  public static final String  PAGE_ADMINISTRACION                           = "administracion";
  public static final String     PAGE_PARAMETROS                            = "parametros";
  public static final String     PAGE_USUARIOS                              = "usuario";
  public static final String     PAGE_GRUPOS_USUARIOS                       = "grupos_usuarios";
  public static final String  PAGE_CLASIFICACION                            = "clasificacion";
  public static final String     PAGE_FONDOS                                = "fondo";
  public static final String     PAGE_OFICINAS                              = "oficina";
  public static final String     PAGE_SERIES                                = "serie";
  public static final String     PAGE_SUBSERIES                             = "subserie";
  public static final String     PAGE_TIPOS_DOCUMENTALES                    = "tipos_documentales";
  public static final String  PAGE_ADMIN_EXPEDIENTES                        = "admin_expediente";
  public static final String     PAGE_EXPEDIENTES                           = "expediente";
  public static final String     PAGE_SUBEXPEDIENTES                        = "sub_expediente";
  public static final String     PAGE_VOLUMENES                             = "volumen";
  public static final String     PAGE_INDICE                                = "indice";
  public static final String     PAGE_EXPORTACION                           = "exporta_expedientes";
  public static final String     PAGE_IMPORTACION                           = "importa_expedientes";
  public static final String     PAGE_COPIA_DOCUMENTOS                      = "copia_documento";
  public static final String     PAGE_TRANSFER_DOCUMENTOS                   = "transferencia_documento";
  public static final String  PAGE_TRAMITE                                  = "tramite";
  public static final String     PAGE_BANDEJA                               = "bandeja";
  public static final String     PAGE_CLASIFICACION_DOCUMENTOS              = "clasifica_documento";
  public static final String     PAGE_RETORNO                               = "retorno";
  public static final String     PAGE_RE_ENVIO                              = "reEnvio";
  public static final String     PAGE_BORRADORES                            = "borrador";
  public static final String     PAGE_FIRMA                                 = "firma_documento";
  public static final String     PAGE_ENVIO                                 = "envio_interno";
  public static final String  PAGE_RECEPCION                                = "recepcion";
  public static final String     PAGE_RECEPCION_DOCUMENTOS                  = "recibe_doc";
  public static final String     PAGE_RECEPCION_E_MAIL                      = "recibe_email";
  public static final String     PAGE_DIGITALIZACION                        = "digitalizacion";
  public static final String     PAGE_DIRECCIONAMIENTO                      = "enruta_documento";
  public static final String  PAGE_CORRESPONDENCIA_EXTERNA                  = "correspondencia_externa";
  public static final String     PAGE_REGISTRO_ENVIOS                       = "consolida_envios";
  public static final String     PAGE_ENVIO_EXTERNO                         = "envio_externo";
  public static final String     PAGE_CONFIRMACION_ENVIO                    = "confirmacion_envio";
  public static final String  PAGE_CONSULTA                                 = "consulta";
  public static final String     PAGE_CONSULTA_DOCUMENTOS                   = "consulta_documentos";
  public static final String        PAGE_CONSULTA_LIBRE                     = "consulta_documentos_libre";
  public static final String        PAGE_CONSULTA_METADATOS                 = "consulta_documentos_metadatos";
  public static final String     PAGE_CONSULTA_EXPEDIENTES                  = "consulta_expedientes";
  public static final String        PAGE_CONSULTA_EXPEDIENTES_LIBRE         = "consulta_expedientes_libre";
  public static final String        PAGE_CONSULTA_EXPEDIENTES_METADATOS     = "consulta_expedientes_metadatos";
  public static final String        PAGE_CONSULTA_EXPEDIENTES_CLASIFICACION = "consulta_expedientes_clasificacion";
  public static final String  PAGE_PROCESOS                                 = "proceso";
  public static final String     PAGE_EJECUCION_PROCESO                     = "ejecucion_proceso";
  public static final String     PAGE_DEFINICION_PROCESO                    = "definicion_proceso";
  public static final String  PAGE_ARCHIVO                                  = "archivo";
  public static final String     PAGE_LOCALES                               = "local";
  public static final String     PAGE_TRANSFERENCIA                         = "prepara_transferencia_archivo";
  public static final String     PAGE_RECIBO_TRANSFERENCIA                  = "recibe_transferencia_archivo";
  public static final String     PAGE_LOCALIZACION                          = "localizacion";
  public static final String     PAGE_PRESTAMO                              = "prestamos";
  public static final String        PAGE_PRESTAMO_EXPEDIENTE                = "prestamo_expediente";
  public static final String        PAGE_DEVOLUCION                         = "retorno_expediente";
  public static final String     PAGE_INDICES_ARCHIVO                       = "indice_archivo";

  public static final String  TITLE_CLIENTES                                 = "Clientes";
  public static final String     TITLE_TENANTS                               = "Tenants";
  public static final String  TITLE_SEGURIDAD                                = "Seguridad";
  public static final String     TITLE_OBJETOS                               = "Operaciones";
  public static final String     TITLE_INFORMACION                           = "Información";
  public static final String     TITLE_ROLES                                 = "Roles";
  public static final String     TITLE_PERMISOS_EJECUCION                    = "Permisos de ejecución";
  public static final String     TITLE_PERMISOS_ACCESO                       = "Permisos de acceso";
  public static final String  TITLE_ADMINISTRACION                           = "Administración";
  public static final String     TITLE_PARAMETROS                            = "Parámetros";
  public static final String     TITLE_USUARIOS                              = "Usuarios";
  public static final String     TITLE_GRUPOS_USUARIOS                       = "Grupos de usuarios";
  public static final String  TITLE_CLASIFICACION                            = "Clasificación";
  public static final String     TITLE_FONDOS                                = "Fondos";
  public static final String     TITLE_OFICINAS                              = "Oficinas";
  public static final String     TITLE_SERIES                                = "Series";
  public static final String     TITLE_SUBSERIES                             = "Subseries";
  public static final String     TITLE_TIPOS_DOCUMENTALES                    = "Tipos documentales";
  public static final String  TITLE_ADMIN_EXPEDIENTES                        = "Gestión expedientes";
  public static final String     TITLE_EXPEDIENTES                           = "Expedientes mayores";
  public static final String     TITLE_SUBEXPEDIENTES                        = "Sub-expedientes";
  public static final String     TITLE_VOLUMENES                             = "Volúmenes";
  public static final String     TITLE_INDICE                                = "Índice de expedientes";
  public static final String     TITLE_EXPORTACION                           = "Exportación de expedientes";
  public static final String     TITLE_IMPORTACION                           = "Importación de expedientes";
  public static final String     TITLE_COPIA_DOCUMENTOS                      = "Copia documento a otro expediente";
  public static final String     TITLE_TRANSER_DOCUMENTOS                    = "Transferencia de documento a otro expediente";
  public static final String  TITLE_TRAMITE                                  = "Tramite de documentos";
  public static final String     TITLE_BANDEJA                               = "Bandeja personal";
  public static final String     TITLE_CLASIFICACION_DOCUMENTOS              = "Clasificación de documento";
  public static final String     TITLE_RETORNO                               = "Devolución de documento";
  public static final String     TITLE_RE_ENVIO                              = "Re-envío de documento";
  public static final String     TITLE_BORRADORES                            = "Carga borrador de documento";
  public static final String     TITLE_FIRMA                                 = "Firma de documento";
  public static final String     TITLE_ENVIO                                 = "Ordena envío de documento";
  public static final String  TITLE_RECEPCION                                = "Recepción de documentos";
  public static final String     TITLE_RECEPCION_DOCUMENTOS                  = "Recepción en ventanilla";
  public static final String     TITLE_RECEPCION_E_MAIL                      = "Recepción correo electrónico";
  public static final String     TITLE_DIGITALIZACION                        = "Digitalización";
  public static final String     TITLE_DIRECCIONAMIENTO                      = "Enrutamiento de documentos";
  public static final String  TITLE_CORRESPONDENCIA_EXTERNA                  = "Envío de documentos";
  public static final String     TITLE_REGISTRO_ENVIOS                       = "Consolidación envíos externos";
  public static final String     TITLE_ENVIO_EXTERNO                         = "Envía correspondencia externa";
  public static final String     TITLE_CONFIRMACION_ENVIO                    = "Confirmación de recepción";
  public static final String  TITLE_CONSULTA                                 = "Consulta";
  public static final String     TITLE_DOCUMENTOS                            = "Consulta de documentos";
  public static final String        TITLE_CONSULTA_LIBRE                     = "Consulta libre documentos";
  public static final String        TITLE_CONSULTA_METADATOS                 = "Consulta documentos según metadatos";
  public static final String     TITLE_CONSULTA_EXPEDIENTES                  = "Consulta de expedientes";
  public static final String        TITLE_CONSULTA_EXPEDIENTES_LIBRE         = "Consulta de expedientes según texto libre";
  public static final String        TITLE_CONSULTA_EXPEDIENTES_METADATOS     = "Consulta de expedientes según metadatos";
  public static final String        TITLE_CONSULTA_EXPEDIENTES_CLASIFICACION = "Consulta de expedientes según clasificación";
  public static final String  TITLE_PROCESOS                                 = "Procesos";
  public static final String     TITLE_EJECUCION_PROCESO                     = "Ejecución de proceso";
  public static final String     TITLE_DEFINICION_PROCESO                    = "Definición de proceso";
  public static final String  TITLE_ARCHIVO                                  = "Archivo";
  public static final String     TITLE_LOCALES                               = "Locales";
  public static final String     TITLE_TRANSFERENCIA                         = "Preparación de transferencia";
  public static final String     TITLE_RECIBO_TRANSFERENCIA                  = "Recepción de transferencia";
  public static final String     TITLE_LOCALIZACION                          = "Localización de documentos";
  public static final String     TITLE_PRESTAMO                              = "Préstamos";
  public static final String        TITLE_PRESTAMO_EXPEDIENTE                = "Préstamo de expedientes";
  public static final String        TITLE_DEVOLUCION                         = "Retorno de expediente";
  public static final String     TITLE_INDICES_ARCHIVO                       = "Indice de archivo";


   public static final String[] ORDER_SORT_FIELDS = {"dueDate", "dueTime", "id"};
   public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

   public static final String VIEWPORT = "width=device-width, minimum-scale=1, initial-scale=1, user-scalable=yes, viewport-fit=cover";

   // Mutable for testing.
   public static int NOTIFICATION_DURATION = 4000;

}//BakeryConst
