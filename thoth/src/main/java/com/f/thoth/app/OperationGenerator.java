package com.f.thoth.app;

import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Operation;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.repositories.OperationRepository;
import com.f.thoth.ui.utils.Constant;

/**
 * Representa el conjunto de operaciones permitidas a un usuario, por menús u otro procedimiiento ejecutable
 */
public class OperationGenerator
{
   private OperationRepository   operationRepository;
   
   public OperationGenerator( OperationRepository operationRepository)
   {
      this.operationRepository = operationRepository;
   }
   
   @SuppressWarnings("unused")
   public void registerOperations(Tenant tenant)
   {
      Operation obj01 = createOperation( tenant, Constant.TITLE_CLIENTES                                , null );  // Clientes
      Operation obj02 = createOperation( tenant,    Constant.TITLE_TENANTS                              , obj01);  // Fondo
      Operation obj03 = createOperation( tenant, Constant.TITLE_SEGURIDAD                               , null );  // Seguridad
      Operation obj04 = createOperation( tenant,    Constant.TITLE_OPERATIONS                           , obj03);  // Operaciones
      Operation obj05 = createOperation( tenant,    Constant.TITLE_INFORMACION                          , obj03);  // Informacion
      Operation obj06 = createOperation( tenant,    Constant.TITLE_ROLES                                , obj03);  // Roles
      Operation obj07 = createOperation( tenant,    Constant.TITLE_PERMISOS_EJECUCION                   , obj03);  // Permisos de ejecucion
      Operation obj08 = createOperation( tenant,    Constant.TITLE_PERMISOS_ACCESO                      , obj03);  // Permisos de acceso
      Operation obj09 = createOperation( tenant, Constant.TITLE_ADMINISTRACION                          , null );  // Administracion
      Operation obj10 = createOperation( tenant,    Constant.TITLE_PARAMETROS                           , obj09);  // Parametros
      Operation obj11 = createOperation( tenant,    Constant.TITLE_USUARIOS                             , obj09);  // Usuarios
      Operation obj12 = createOperation( tenant,    Constant.TITLE_GRUPOS_USUARIOS                      , obj09);  // Grupos de usuarios
      Operation obj13 = createOperation( tenant, Constant.TITLE_PROPIEDADES                             , null );  // Propiedades
      Operation obj14 = createOperation( tenant,    Constant.TITLE_METADATA                             , obj13);  // Metadatos
      Operation obj15 = createOperation( tenant,    Constant.TITLE_ESQUEMAS_METADATA                    , obj13);  // Esquemas de metadatos
      Operation obj16 = createOperation( tenant,    Constant.TITLE_TIPOS_DOCUMENTALES                   , obj13);  // Tipos documentales
      Operation obj17 = createOperation( tenant, Constant.TITLE_CLASIFICACION                           , null );  // Clasificacion
      Operation obj18 = createOperation( tenant,    Constant.TITLE_NIVELES                              , obj17);  // Niveles
      Operation obj19 = createOperation( tenant,    Constant.TITLE_RETENCION                            , obj17);  // Niveles
      Operation obj20 = createOperation( tenant,    Constant.TITLE_ESQUEMAS_CLASIFICACION               , obj17);  // Esquemas de clasificación
      Operation obj21 = createOperation( tenant, Constant.TITLE_ADMIN_EXPEDIENTES                       , null );  // Gestion expedientes
      Operation obj22 = createOperation( tenant,    Constant.TITLE_EXPEDIENTES_SELECTOR_CLASE           , obj21);  // Seleccion clase a que pertenece expediente
      Operation obj23 = createOperation( tenant,    Constant.TITLE_SUBEXPEDIENTES                       , obj21);  // Sub-expedientes
      Operation obj24 = createOperation( tenant,    Constant.TITLE_VOLUMENES                            , obj21);  // Volumenes
      Operation obj25 = createOperation( tenant,    Constant.TITLE_INDICE                               , obj21);  // Indice de expedientes
      Operation obj26 = createOperation( tenant,    Constant.TITLE_EXPORTACION                          , obj21);  // Exportacion de expedientes
      Operation obj27 = createOperation( tenant,    Constant.TITLE_IMPORTACION                          , obj21);  // Importacion de expedientes
      Operation obj28 = createOperation( tenant,    Constant.TITLE_COPIA_DOCUMENTOS                     , obj21);  // Copia documento a otro expediente
      Operation obj29 = createOperation( tenant,    Constant.TITLE_TRANSER_DOCUMENTOS                   , obj21);  // Transferencia de documento a otro expediente
      Operation obj30 = createOperation( tenant, Constant.TITLE_TRAMITE                                 , null );  // Tramite de documentos
      Operation obj31 = createOperation( tenant,    Constant.TITLE_BANDEJA                              , obj30);  // Bandeja personal
      Operation obj32 = createOperation( tenant,    Constant.TITLE_CLASIFICACION_DOCUMENTOS             , obj30);  // Clasificacion de documento
      Operation obj33 = createOperation( tenant,    Constant.TITLE_RETORNO                              , obj30);  // Devolucion de documento
      Operation obj34 = createOperation( tenant,    Constant.TITLE_RE_ENVIO                             , obj30);  // Re-envio de documento
      Operation obj35 = createOperation( tenant,    Constant.TITLE_BORRADORES                           , obj30);  // Carga borrador de documento
      Operation obj36 = createOperation( tenant,    Constant.TITLE_FIRMA                                , obj30);  // Firma de documento
      Operation obj37 = createOperation( tenant,    Constant.TITLE_ENVIO                                , obj30);  // Ordena envio de documento
      Operation obj38 = createOperation( tenant, Constant.TITLE_RECEPCION                               , null );  // Recepcion de documentos
      Operation obj39 = createOperation( tenant,    Constant.TITLE_RECEPCION_DOCUMENTOS                 , obj38);  // Recepcion en ventanilla
      Operation obj40 = createOperation( tenant,    Constant.TITLE_RECEPCION_E_MAIL                     , obj38);  // Recepcion correo electronico
      Operation obj41 = createOperation( tenant,    Constant.TITLE_DIGITALIZACION                       , obj38);  // Digitalizacion
      Operation obj42 = createOperation( tenant,    Constant.TITLE_DIRECCIONAMIENTO                     , obj38);  // Enrutamiento de documentos
      Operation obj43 = createOperation( tenant, Constant.TITLE_CORRESPONDENCIA_EXTERNA                 , null );  // Envio de documentos
      Operation obj44 = createOperation( tenant,    Constant.TITLE_REGISTRO_ENVIOS                      , obj43);  // Consolidacion envoos externos
      Operation obj45 = createOperation( tenant,    Constant.TITLE_ENVIO_EXTERNO                        , obj43);  // Envia correspondencia externa
      Operation obj46 = createOperation( tenant,    Constant.TITLE_CONFIRMACION_ENVIO                   , obj43);  // Confirmacion de recepcion
      Operation obj47 = createOperation( tenant, Constant.TITLE_CONSULTA                                , null );  // Consulta
      Operation obj48 = createOperation( tenant,    Constant.TITLE_DOCUMENTOS                           , obj47);  // Consulta de documentos
      Operation obj49 = createOperation( tenant,       Constant.TITLE_CONSULTA_LIBRE                    , obj48);  // Consulta libre documentos
      Operation obj50 = createOperation( tenant,       Constant.TITLE_CONSULTA_METADATOS                , obj48);  // Consulta documentos segun metadatos
      Operation obj51 = createOperation( tenant,    Constant.TITLE_CONSULTA_EXPEDIENTES                 , obj47);  // Consulta de expedientes
      Operation obj52 = createOperation( tenant,       Constant.TITLE_CONSULTA_EXPEDIENTES_LIBRE        , obj51);  // Consulta de expedientes segun texto libre
      Operation obj53 = createOperation( tenant,       Constant.TITLE_CONSULTA_EXPEDIENTES_METADATOS    , obj51);  // Consulta de expedientes segun metadatos
      Operation obj54 = createOperation( tenant,       Constant.TITLE_CONSULTA_EXPEDIENTES_CLASIFICACION, obj51);  // Consulta de expedientes segun clasificacion
      Operation obj55 = createOperation( tenant, Constant.TITLE_PROCESOS                                , null );  // Procesos
      Operation obj56 = createOperation( tenant,    Constant.TITLE_EJECUCION_PROCESO                    , obj55);  // Ejecucion de proceso
      Operation obj57 = createOperation( tenant,    Constant.TITLE_DEFINICION_PROCESO                   , obj55);  // Definicion de proceso
      Operation obj58 = createOperation( tenant, Constant.TITLE_ARCHIVO                                 , null );  // Archivo
      Operation obj59 = createOperation( tenant,    Constant.TITLE_LOCALES                              , obj58);  // Locales
      Operation obj60 = createOperation( tenant,    Constant.TITLE_TRANSFERENCIA                        , obj58);  // Preparacion de transferencia
      Operation obj61 = createOperation( tenant,    Constant.TITLE_RECIBO_TRANSFERENCIA                 , obj58);  // Recepcion de transferencia
      Operation obj62 = createOperation( tenant,    Constant.TITLE_LOCALIZACION                         , obj58);  // Localizacion de documentos
      Operation obj63 = createOperation( tenant,    Constant.TITLE_PRESTAMO                             , obj58);  // Prestamos
      Operation obj64 = createOperation( tenant,       Constant.TITLE_PRESTAMO_EXPEDIENTE               , obj63);  // Prestamo de expedientes
      Operation obj65 = createOperation( tenant,       Constant.TITLE_DEVOLUCION                        , obj63);  // Retorno de expediente
      Operation obj66 = createOperation( tenant,    Constant.TITLE_INDICES_ARCHIVO                      , obj58);  // Indice de archivo

   }//registerOperations
   

   private Operation createOperation( Tenant tenant, String name, Operation owner)
   {
      Operation operation = new Operation( name, new ObjectToProtect(), owner);
      operation.setTenant(tenant);
      Operation savedOperation = operationRepository.save(operation);
      return savedOperation;
   }//createOperation


}//OperationGenerator
