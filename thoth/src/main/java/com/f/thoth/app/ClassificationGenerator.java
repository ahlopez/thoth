package com.f.thoth.app;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.f.thoth.Parm;
import com.f.thoth.backend.data.entity.util.TextUtil;
import com.f.thoth.backend.data.gdoc.classification.Classification;
import com.f.thoth.backend.data.gdoc.classification.Level;
import com.f.thoth.backend.data.gdoc.document.jackrabbit.NodeType;
import com.f.thoth.backend.data.gdoc.metadata.Schema;
import com.f.thoth.backend.data.gdoc.metadata.SchemaValues;
import com.f.thoth.backend.data.gdoc.numerator.Numerator;
import com.f.thoth.backend.data.security.ObjectToProtect;
import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.data.security.User;
import com.f.thoth.backend.jcr.Repo;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.LevelRepository;
import com.f.thoth.backend.repositories.SchemaRepository;
import com.f.thoth.ui.utils.Constant;

/**
 * Genera un ejemplo de esquema de clasificación (Solo por propósitos de prueba)
 */
public class ClassificationGenerator implements HasLogger
{
   private ClassificationRepository  claseRepository;
   private LevelRepository           levelRepository;
   private SchemaRepository          schemaRepository;
   private Numerator                 numerator;
   private Level[]                   level;
   private int                       nClasses=0;
   private int                       nSequences=0;
   private Set<String>               seqNumbers;
   private String                    currentUser;

   public ClassificationGenerator( User user, ClassificationRepository claseRepository,
                                   LevelRepository levelRepository,
                                   SchemaRepository schemaRepository,
                                   Numerator        numerator,
                                   Level[] level
                                  )
   {
      this.claseRepository    = claseRepository;
      this.levelRepository    = levelRepository;
      this.schemaRepository   = schemaRepository;
      this.numerator          = numerator;
      this.level              = level;
      this.seqNumbers         = new TreeSet<>();
      this.currentUser        = user.getEmail();

   }//ClassificationGenerator constructor

   @SuppressWarnings("unused")
   public void registerClasses( Tenant tenant) throws RepositoryException, UnknownHostException
   {
      getLogger().info("... Register classification classes");
      String classificationRootPath = initJcrClassification(tenant, NodeType.CLASSIFICATION.getCode());
      Classification clase001 = createClass( tenant, classificationRootPath, Constant.TITLE_SEDE_CORPORATIVA                                , "01", /* 01*/       level[0], null);      //   Sede Corporativa
      Classification clase002 = createClass( tenant, classificationRootPath,   Constant.TITLE_CRP_OFICINA_GERENCIA_GENERAL                  , "01", /* 0101*/     level[1], clase001);  //   Corporativa, Gerencia_general
      Classification clase003 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_ACTAS                             , "01", /* 010101*/   level[2], clase002);  //   Corporativa, Actas
      Classification clase004 = createClass( tenant, classificationRootPath,       Constant.TITLE_CRP_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , "01", /* 01010101*/ level[3], clase003);  //   Corporativa, Actas_junta_directiva
      Classification clase005 = createClass( tenant, classificationRootPath,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_GERENCIA        , "02", /* 01010102*/ level[3], clase003);  //   Corporativa, Actas_comite_gerencia
      Classification clase006 = createClass( tenant, classificationRootPath,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_FINANCIERO      , "03", /* 01010103*/ level[3], clase003);  //   Corporativa, Actas_comite_financiero
      Classification clase007 = createClass( tenant, classificationRootPath,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , "04", /* 01010104*/ level[3], clase003);  //   Corporativa, Actas_comite_administrativo
      Classification clase008 = createClass( tenant, classificationRootPath,       Constant.TITLE_CRP_SUBSERIE_ACTAS_COMITE_OPERACIONES     , "05", /* 01010105*/ level[3], clase003);  //   Corporativa, Actas_comite_operaciones
      Classification clase010 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_PLANES                            , "02", /* 010102*/   level[2], clase002);  //   Corporativa, Planes
      Classification clase011 = createClass( tenant, classificationRootPath,       Constant.TITLE_CRP_SUBSERIE_PLAN_OPERATIVO               , "01", /* 01010201*/ level[3], clase010);  //   Corporativa, Plan_operativo
      Classification clase012 = createClass( tenant, classificationRootPath,       Constant.TITLE_CRP_SUBSERIE_PLAN_FINANCIERO              , "02", /* 01010202*/ level[3], clase010);  //   Corporativa, Plan_financiero
      Classification clase013 = createClass( tenant, classificationRootPath,       Constant.TITLE_CRP_SUBSERIE_PRESUPUESTO                  , "03", /* 01010203*/ level[3], clase010);  //   Corporativa, Presupuesto
      Classification clase014 = createClass( tenant, classificationRootPath,   Constant.TITLE_CRP_OFICINA_OPERACIONES                       , "02", /* 0102*/     level[1], clase001);  //   Corporativa, Subgerencia de Operaciones
      Classification clase015 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_ACTAS_OPERACIONES                 , "01", /* 010201*/   level[2], clase014);  //   Corporativa, Actas Operaciones
      Classification clase016 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , "01", /* 01020101*/ level[3], clase015);  //   Corporativa, Actas Comite calidad
      Classification clase017 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , "02", /* 01020102*/ level[3], clase015);  //   Corporativa, Actas Comite planeacion
      Classification clase018 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_CONTRATOS                         , "02", /* 010202*/   level[2], clase014);  //   Corporativa, Contratos
      Classification clase019 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_OPER_CONTRATOS_OPERACION    , "01", /* 01020201*/ level[3], clase018);  //   Corporativa, Contratos de Operacion
      Classification clase020 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_OPER_CONTRATOS_INVERSION    , "02", /* 01020202*/ level[3], clase018);  //   Corporativa, Contratos de Inversion
      Classification clase021 = createClass( tenant, classificationRootPath,   Constant.TITLE_CRP_OFICINA_FINANCIERA                        , "03", /* 0103*/     level[1], clase001);  //   Corporativa, Subgerencia Financiera
      Classification clase022 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_PRESUPUESTO                       , "01", /* 010301*/   level[2], clase021);  //   Corporativa, Presupuesto
      Classification clase023 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_FIN_PLANEACION_PPTAL        , "01", /* 01030101*/ level[3], clase022);  //   Corporativa, Planeacion Presupuestal
      Classification clase024 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_FIN_EJECUCION_PPTAL         , "02", /* 01030102*/ level[3], clase022);  //   Corporativa, Ejecucion Presupuestal
      Classification clase025 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_TESORERIA                         , "02", /* 010302*/   level[2], clase021);  //   Corporativa, Tesoreria
      Classification clase026 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_FIN_PAGADURIA               , "01", /* 01030201*/ level[3], clase025);  //   Corporativa, Pagaduria
      Classification clase027 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_FIN_INVERSIONES             , "02", /* 01030202*/ level[3], clase025);  //   Corporativa, Inversiones
      Classification clase028 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_CONTABILIDAD                      , "03", /* 010303*/   level[2], clase021);  //   Corporativa, Contabilidad
      Classification clase029 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , "01", /* 01030301*/ level[3], clase028);  //   Corporativa, Estados Financieros
      Classification clase030 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_FIN_LIBROS_CONTABLES        , "02", /* 01030302*/ level[3], clase028);  //   Corporativa, Libros contables
      Classification clase031 = createClass( tenant, classificationRootPath,   Constant.TITLE_CRP_OFICINA_PERSONAL                          , "04", /* 0104*/     level[1], clase001);  //   Corporativa, Subgerencia de Personal
      Classification clase032 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_HOJAS_DE_VIDA                     , "01", /* 010401*/   level[2], clase031);  //   Corporativa, Hojas de vida
      Classification clase033 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_PER_CANDIDATOS              , "01", /* 01040101*/ level[3], clase032);  //   Corporativa, Candidatos de personal
      Classification clase034 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_PER_PERSONAL_ACTIVO         , "02", /* 01040102*/ level[3], clase032);  //   Corporativa, Personal activo
      Classification clase035 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_PER_PENSIONADOS             , "03", /* 01040103*/ level[3], clase032);  //   Corporativa, Pensionados
      Classification clase036 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_SANCIONES                         , "02", /* 010402*/   level[2], clase031);  //   Corporativa, Sanciones de personal
      Classification clase037 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_PER_INVESTIGACIONES         , "01", /* 01040201*/ level[3], clase036);  //   Corporativa, Investigaciones disciplinarias
      Classification clase038 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_PER_FALLOS_DE_PERSONAL      , "02", /* 01040202*/ level[3], clase036);  //   Corporativa, Fallos de personal
      Classification clase039 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_EVALUACIONES                      , "03", /* 010403*/   level[2], clase031);  //   Corporativa, Evaluaciones de personal
      Classification clase040 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_PER_DESEMPENO               , "01", /* 01040301*/ level[3], clase039);  //   Corporativa, Evaluaciones de desempeeo
      Classification clase041 = createClass( tenant, classificationRootPath,   Constant.TITLE_CRP_OFICINA_JURIDICA                          , "05", /* 0105*/     level[1], clase001);  //   Corporativa, Subgerencia Juridica
      Classification clase042 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_PROCESOS                          , "01", /* 010501*/   level[2], clase041);  //   Corporativa, Procesos juridicos
      Classification clase043 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_JUR_DEMANDAS                , "01", /* 01050101*/ level[3], clase042);  //   Corporativa, Demandas en curso
      Classification clase044 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_JUR_FALLOS_JUDICIALES       , "02", /* 01050102*/ level[3], clase042);  //   Corporativa, Demandas en curso
      Classification clase045 = createClass( tenant, classificationRootPath,   Constant.TITLE_CRP_OFICINA_ADMINISTRACION                    , "06", /* 0106*/     level[1], clase001);  //   Corporativa, Subgerencia Administrativa
      Classification clase046 = createClass( tenant, classificationRootPath,     Constant.TITLE_CRP_SERIE_ACTIVOS_FIJOS                     , "01", /* 010601*/   level[2], clase045);  //   Corporativa, Activos fijos
      Classification clase047 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_ADM_EDIFICACIONES           , "01", /* 01060101*/ level[3], clase046);  //   Corporativa, Edificaciones
      Classification clase048 = createClass( tenant, classificationRootPath,        Constant.TITLE_CRP_SUBSERIE_ADM_SERVICIOS               , "02", /* 01060102*/ level[3], clase046);  //   Corporativa, Servicios publicos

      Classification clase049 = createClass( tenant, classificationRootPath, Constant.TITLE_SEDE_BOGOTA                                     , "02", /* 02*/       level[0], null);      //   Sede Bogota
      Classification clase050 = createClass( tenant, classificationRootPath,   Constant.TITLE_BOG_OFICINA_SUBGERENCIA                       , "01", /* 0201*/     level[1], clase049);  //   Gerencia Bogota
      Classification clase051 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_ACTAS                             , "01", /* 020101*/   level[2], clase050);  //   Bogota, Actas
      Classification clase052 = createClass( tenant, classificationRootPath,       Constant.TITLE_BOG_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , "01", /* 02010101*/ level[3], clase051);  //   Bogota, Actas_junta_directiva
      Classification clase053 = createClass( tenant, classificationRootPath,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_GERENCIA        , "02", /* 02010102*/ level[3], clase051);  //   Bogota, Actas_comite_gerencia
      Classification clase054 = createClass( tenant, classificationRootPath,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_FINANCIERO      , "03", /* 02010103*/ level[3], clase051);  //   Bogota, Actas_comite_financiero
      Classification clase055 = createClass( tenant, classificationRootPath,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , "04", /* 02010104*/ level[3], clase051);  //   Bogota, Actas_comite_administrativo
      Classification clase056 = createClass( tenant, classificationRootPath,       Constant.TITLE_BOG_SUBSERIE_ACTAS_COMITE_OPERACIONES     , "05", /* 02010105*/ level[3], clase051);  //   Bogota, Actas_comite_operaciones
      Classification clase058 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_PLANES                            , "02", /* 020102*/   level[2], clase050);  //   Bogota, Planes
      Classification clase059 = createClass( tenant, classificationRootPath,       Constant.TITLE_BOG_SUBSERIE_PLAN_OPERATIVO               , "01", /* 02010201*/ level[3], clase058);  //   Bogota, Plan_operativo
      Classification clase060 = createClass( tenant, classificationRootPath,       Constant.TITLE_BOG_SUBSERIE_PLAN_FINANCIERO              , "02", /* 02010202*/ level[3], clase058);  //   Bogota, Plan_financiero
      Classification clase061 = createClass( tenant, classificationRootPath,       Constant.TITLE_BOG_SUBSERIE_PRESUPUESTO                  , "03", /* 02010203*/ level[3], clase058);  //   Bogota, Presupuesto
      Classification clase062 = createClass( tenant, classificationRootPath,   Constant.TITLE_BOG_OFICINA_OPERACIONES                       , "02", /* 0202*/     level[1], clase049);  //   Bogota, Subgerencia de Operaciones
      Classification clase063 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_ACTAS_OPERACIONES                 , "01", /* 020201*/   level[2], clase062);  //   Bogota, Actas Operaciones
      Classification clase064 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , "01", /* 02020101*/ level[3], clase063);  //   Bogota, Actas Comite calidad
      Classification clase065 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , "02", /* 02020102*/ level[3], clase063);  //   Bogota, Actas Comite planeacion
      Classification clase066 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_CONTRATOS                         , "02", /* 020202*/   level[2], clase062);  //   Bogota, Contratos
      Classification clase067 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_OPER_CONTRATOS_OPERACION    , "01", /* 02020201*/ level[3], clase066);  //   Bogota, Contratos de Operacion
      Classification clase068 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_OPER_CONTRATOS_INVERSION    , "02", /* 02020202*/ level[3], clase066);  //   Bogota, Contratos de Inversion
      Classification clase069 = createClass( tenant, classificationRootPath,   Constant.TITLE_BOG_OFICINA_FINANCIERA                        , "03", /* 0203*/     level[1], clase049);  //   Bogota, Subgerencia Financiera
      Classification clase070 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_PRESUPUESTO                       , "01", /* 020301*/   level[2], clase069);  //   Bogota, Presupuesto
      Classification clase071 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_FIN_PLANEACION_PPTAL        , "01", /* 02030101*/ level[3], clase070);  //   Bogota, Planeacion Presupuestal
      Classification clase072 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_FIN_EJECUCION_PPTAL         , "02", /* 02030102*/ level[3], clase070);  //   Bogota, Ejecucion Presupuestal
      Classification clase073 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_TESORERIA                         , "02", /* 020302*/   level[2], clase069);  //   Bogota, Tesoreria
      Classification clase074 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_FIN_PAGADURIA               , "01", /* 02030201*/ level[3], clase073);  //   Bogota, Pagaduria
      Classification clase075 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_FIN_INVERSIONES             , "02", /* 02030202*/ level[3], clase073);  //   Bogota, Inversiones
      Classification clase076 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_CONTABILIDAD                      , "03", /* 020303*/   level[2], clase069);  //   Bogota, Contabilidad
      Classification clase077 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , "01", /* 02030301*/ level[3], clase076);  //   Bogota, Estados Financieros
      Classification clase078 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_FIN_LIBROS_CONTABLES        , "02", /* 02030302*/ level[3], clase076);  //   Bogota, Libros contables
      Classification clase079 = createClass( tenant, classificationRootPath,   Constant.TITLE_BOG_OFICINA_PERSONAL                          , "04", /* 0204*/     level[1], clase049);  //   Bogota, Subgerencia de Personal
      Classification clase080 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_HOJAS_DE_VIDA                     , "01", /* 020401*/   level[2], clase079);  //   Bogota, Hojas de vida
      Classification clase081 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_PER_CANDIDATOS              , "01", /* 02040101*/ level[3], clase080);  //   Bogota, Candidatos de personal
      Classification clase082 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_PER_PERSONAL_ACTIVO         , "02", /* 02040102*/ level[3], clase080);  //   Bogota, Personal activo
      Classification clase083 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_PER_PENSIONADOS             , "03", /* 02040103*/ level[3], clase080);  //   Bogota, Pensionados
      Classification clase084 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_SANCIONES                         , "02", /* 020402*/   level[2], clase079);  //   Bogota, Sanciones de personal
      Classification clase085 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_PER_INVESTIGACIONES         , "01", /* 02040201*/ level[3], clase084);  //   Bogota, Investigaciones disciplinarias
      Classification clase086 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_PER_FALLOS_DE_PERSONAL      , "02", /* 02040202*/ level[3], clase084);  //   Bogota, Fallos de personal
      Classification clase087 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_EVALUACIONES                      , "03", /* 020403*/   level[2], clase079);  //   Bogota, Evaluaciones de personal
      Classification clase088 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_PER_DESEMPENO               , "01", /* 02040301*/ level[3], clase087);  //   Bogota, Evaluaciones de desempeeo
      Classification clase089 = createClass( tenant, classificationRootPath,   Constant.TITLE_BOG_OFICINA_JURIDICA                          , "05", /* 0205*/     level[1], clase049);  //   Bogota, Subgerencia Juridica
      Classification clase090 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_PROCESOS                          , "01", /* 020501*/   level[2], clase089);  //   Bogota, Procesos juridicos
      Classification clase091 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_JUR_DEMANDAS                , "01", /* 02050101*/ level[3], clase090);  //   Bogota, Demandas en curso
      Classification clase092 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_JUR_FALLOS_JUDICIALES       , "02", /* 02050102*/ level[3], clase090);  //   Bogota, Demandas en curso
      Classification clase093 = createClass( tenant, classificationRootPath,   Constant.TITLE_BOG_OFICINA_ADMINISTRACION                    , "06", /* 0206*/     level[1], clase049);  //   Bogota, Subgerencia Administrativa
      Classification clase094 = createClass( tenant, classificationRootPath,     Constant.TITLE_BOG_SERIE_ACTIVOS_FIJOS                     , "01", /* 020601*/   level[2], clase093);  //   Bogota, Activos fijos
      Classification clase095 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_ADM_EDIFICACIONES           , "01", /* 02060101*/ level[3], clase094);  //   Bogota, Edificaciones
      Classification clase096 = createClass( tenant, classificationRootPath,        Constant.TITLE_BOG_SUBSERIE_ADM_SERVICIOS               , "02", /* 02060102*/ level[3], clase094);  //   Bogota, Servicios publicos

      Classification clase097 = createClass( tenant, classificationRootPath, Constant.TITLE_SEDE_MEDELLIN                                   , "03", /* 03*/       level[0], null);      //   Sede Medellin
      Classification clase098 = createClass( tenant, classificationRootPath,   Constant.TITLE_MED_OFICINA_SUBGERENCIA                       , "01", /* 0301*/     level[1], clase097);  //   Gerencia Medellin
      Classification clase099 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_ACTAS                             , "01", /* 030101*/   level[2], clase098);  //   Medellin, Actas
      Classification clase100 = createClass( tenant, classificationRootPath,       Constant.TITLE_MED_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , "01", /* 03010101*/ level[3], clase099);  //   Medellin, Actas_junta_directiva
      Classification clase101 = createClass( tenant, classificationRootPath,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_GERENCIA        , "02", /* 03010102*/ level[3], clase099);  //   Medellin, Actas_comite_gerencia
      Classification clase102 = createClass( tenant, classificationRootPath,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_FINANCIERO      , "03", /* 03010103*/ level[3], clase099);  //   Medellin, Actas_comite_financiero
      Classification clase103 = createClass( tenant, classificationRootPath,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , "04", /* 03010104*/ level[3], clase099);  //   Medellin, Actas_comite_administrativo
      Classification clase104 = createClass( tenant, classificationRootPath,       Constant.TITLE_MED_SUBSERIE_ACTAS_COMITE_OPERACIONES     , "05", /* 03010105*/ level[3], clase099);  //   Medellin, Actas_comite_operaciones
      Classification clase106 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_PLANES                            , "02", /* 030102*/   level[2], clase098);  //   Medellin, Planes
      Classification clase107 = createClass( tenant, classificationRootPath,       Constant.TITLE_MED_SUBSERIE_PLAN_OPERATIVO               , "01", /* 03010201*/ level[3], clase106);  //   Medellin, Plan_operativo
      Classification clase108 = createClass( tenant, classificationRootPath,       Constant.TITLE_MED_SUBSERIE_PLAN_FINANCIERO              , "02", /* 03010202*/ level[3], clase106);  //   Medellin, Plan_financiero
      Classification clase109 = createClass( tenant, classificationRootPath,       Constant.TITLE_MED_SUBSERIE_PRESUPUESTO                  , "03", /* 03010203*/ level[3], clase106);  //   Medellin, Presupuesto
      Classification clase110 = createClass( tenant, classificationRootPath,   Constant.TITLE_MED_OFICINA_OPERACIONES                       , "02", /* 0302*/     level[1], clase097);  //   Medellin, Subgerencia de Operaciones
      Classification clase111 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_ACTAS_OPERACIONES                 , "01", /* 030201*/   level[2], clase110);  //   Medellin, Actas Operaciones
      Classification clase112 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , "01", /* 03020101*/ level[3], clase111);  //   Medellin, Actas Comite calidad
      Classification clase113 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , "02", /* 03020102*/ level[3], clase111);  //   Medellin, Actas Comite planeacion
      Classification clase114 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_CONTRATOS                         , "02", /* 030202*/   level[2], clase110);  //   Medellin, Contratos
      Classification clase115 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_OPER_CONTRATOS_OPERACION    , "01", /* 03020201*/ level[3], clase114);  //   Medellin, Contratos de Operacion
      Classification clase116 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_OPER_CONTRATOS_INVERSION    , "02", /* 03020202*/ level[3], clase114);  //   Medellin, Contratos de Inversion
      Classification clase117 = createClass( tenant, classificationRootPath,   Constant.TITLE_MED_OFICINA_FINANCIERA                        , "03", /* 0303*/     level[1], clase097);  //   Medellin, Subgerencia Financiera
      Classification clase118 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_PRESUPUESTO                       , "01", /* 030301*/   level[2], clase117);  //   Medellin, Presupuesto
      Classification clase119 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_FIN_PLANEACION_PPTAL        , "01", /* 03030101*/ level[3], clase118);  //   Medellin, Planeacion Presupuestal
      Classification clase120 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_FIN_EJECUCION_PPTAL         , "02", /* 03030102*/ level[3], clase118);  //   Medellin, Ejecucion Presupuestal
      Classification clase121 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_TESORERIA                         , "02", /* 030302*/   level[2], clase117);  //   Medellin, Tesoreria
      Classification clase122 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_FIN_PAGADURIA               , "01", /* 03030201*/ level[3], clase121);  //   Medellin, Pagaduria
      Classification clase123 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_FIN_INVERSIONES             , "02", /* 03030202*/ level[3], clase121);  //   Medellin, Inversiones
      Classification clase124 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_CONTABILIDAD                      , "03", /* 030303*/   level[2], clase117);  //   Medellin, Contabilidad
      Classification clase125 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , "01", /* 03030301*/ level[3], clase124);  //   Medellin, Estados Financieros
      Classification clase126 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_FIN_LIBROS_CONTABLES        , "02", /* 03030302*/ level[3], clase124);  //   Medellin, Libros contables
      Classification clase127 = createClass( tenant, classificationRootPath,   Constant.TITLE_MED_OFICINA_PERSONAL                          , "04", /* 0304*/     level[1], clase097);  //   Medellin, Subgerencia de Personal
      Classification clase128 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_HOJAS_DE_VIDA                     , "01", /* 030401*/   level[2], clase127);  //   Medellin, Hojas de vida
      Classification clase129 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_PER_CANDIDATOS              , "01", /* 03040101*/ level[3], clase128);  //   Medellin, Candidatos de personal
      Classification clase130 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_PER_PERSONAL_ACTIVO         , "02", /* 03040102*/ level[3], clase128);  //   Medellin, Personal activo
      Classification clase131 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_PER_PENSIONADOS             , "03", /* 03040103*/ level[3], clase128);  //   Medellin, Pensionados
      Classification clase132 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_SANCIONES                         , "02", /* 030402*/   level[2], clase127);  //   Medellin, Sanciones de personal
      Classification clase133 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_PER_INVESTIGACIONES         , "01", /* 03040201*/ level[3], clase132);  //   Medellin, Investigaciones disciplinarias
      Classification clase134 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_PER_FALLOS_DE_PERSONAL      , "02", /* 03040202*/ level[3], clase132);  //   Medellin, Fallos de personal
      Classification clase135 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_EVALUACIONES                      , "03", /* 030403*/   level[2], clase127);  //   Medellin, Evaluaciones de personal
      Classification clase136 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_PER_DESEMPENO               , "01", /* 03040301*/ level[3], clase135);  //   Medellin, Evaluaciones de desempeeo
      Classification clase137 = createClass( tenant, classificationRootPath,   Constant.TITLE_MED_OFICINA_JURIDICA                          , "05", /* 0305*/     level[1], clase097);  //   Medellin, Subgerencia Juridica
      Classification clase138 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_PROCESOS                          , "01", /* 030501*/   level[2], clase137);  //   Medellin, Procesos juridicos
      Classification clase139 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_JUR_DEMANDAS                , "01", /* 03050101*/ level[3], clase138);  //   Medellin, Demandas en curso
      Classification clase140 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_JUR_FALLOS_JUDICIALES       , "02", /* 03050102*/ level[3], clase138);  //   Medellin, Demandas en curso
      Classification clase141 = createClass( tenant, classificationRootPath,   Constant.TITLE_MED_OFICINA_ADMINISTRACION                    , "06", /* 0306*/     level[1], clase097);  //   Medellin, Subgerencia Administrativa
      Classification clase142 = createClass( tenant, classificationRootPath,     Constant.TITLE_MED_SERIE_ACTIVOS_FIJOS                     , "01", /* 030601*/   level[2], clase141);  //   Medellin, Activos fijos
      Classification clase143 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_ADM_EDIFICACIONES           , "01", /* 03060101*/ level[3], clase142);  //   Medellin, Edificaciones
      Classification clase144 = createClass( tenant, classificationRootPath,        Constant.TITLE_MED_SUBSERIE_ADM_SERVICIOS               , "02", /* 03060102*/ level[3], clase142);  //   Medellin, Servicios publicos

      Classification clase145 = createClass( tenant, classificationRootPath, Constant.TITLE_SEDE_CALI                                       , "04",  /* 04*/       level[0], null);      //   Sede Cali
      Classification clase146 = createClass( tenant, classificationRootPath,   Constant.TITLE_CAL_OFICINA_SUBGERENCIA                       , "01",  /* 0401*/     level[1], clase145);  //   Gerencia Cali
      Classification clase147 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_ACTAS                             , "01",  /* 040101*/   level[2], clase146);  //   Cali, Actas
      Classification clase148 = createClass( tenant, classificationRootPath,       Constant.TITLE_CAL_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , "01",  /* 04010101*/ level[3], clase147);  //   Cali, Actas_junta_directiva
      Classification clase149 = createClass( tenant, classificationRootPath,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_GERENCIA        , "02",  /* 04010102*/ level[3], clase147);  //   Cali, Actas_comite_gerencia
      Classification clase150 = createClass( tenant, classificationRootPath,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_FINANCIERO      , "03",  /* 04010103*/ level[3], clase147);  //   Cali, Actas_comite_financiero
      Classification clase151 = createClass( tenant, classificationRootPath,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , "04",  /* 04010104*/ level[3], clase147);  //   Cali, Actas_comite_administrativo
      Classification clase152 = createClass( tenant, classificationRootPath,       Constant.TITLE_CAL_SUBSERIE_ACTAS_COMITE_OPERACIONES     , "05",  /* 04010105*/ level[3], clase147);  //   Cali, Actas_comite_operaciones
      Classification clase154 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_PLANES                            , "02",  /* 040102*/   level[2], clase146);  //   Cali, Planes
      Classification clase155 = createClass( tenant, classificationRootPath,       Constant.TITLE_CAL_SUBSERIE_PLAN_OPERATIVO               , "01",  /* 04010201*/ level[3], clase154);  //   Cali, Plan_operativo
      Classification clase156 = createClass( tenant, classificationRootPath,       Constant.TITLE_CAL_SUBSERIE_PLAN_FINANCIERO              , "02",  /* 04010202*/ level[3], clase154);  //   Cali, Plan_financiero
      Classification clase157 = createClass( tenant, classificationRootPath,       Constant.TITLE_CAL_SUBSERIE_PRESUPUESTO                  , "03",  /* 04010203*/ level[3], clase154);  //   Cali, Presupuesto
      Classification clase158 = createClass( tenant, classificationRootPath,   Constant.TITLE_CAL_OFICINA_OPERACIONES                       , "02",  /* 0402*/     level[1], clase145);  //   Cali, Subgerencia de Operaciones
      Classification clase159 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_ACTAS_OPERACIONES                 , "01",  /* 040201*/   level[2], clase158);  //   Cali, Actas Operaciones
      Classification clase160 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , "01",  /* 04020101*/ level[3], clase159);  //   Cali, Actas Comite calidad
      Classification clase161 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , "02",  /* 04020102*/ level[3], clase159);  //   Cali, Actas Comite planeacion
      Classification clase162 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_CONTRATOS                         , "02",  /* 040202*/   level[2], clase158);  //   Cali, Contratos
      Classification clase163 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_OPER_CONTRATOS_OPERACION    , "01",  /* 04020201*/ level[3], clase162);  //   Cali, Contratos de Operacion
      Classification clase164 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_OPER_CONTRATOS_INVERSION    , "02",  /* 04020202*/ level[3], clase162);  //   Cali, Contratos de Inversion
      Classification clase165 = createClass( tenant, classificationRootPath,   Constant.TITLE_CAL_OFICINA_FINANCIERA                        , "03",  /* 0403*/     level[1], clase145);  //   Cali, Subgerencia Financiera
      Classification clase166 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_PRESUPUESTO                       , "01",  /* 040301*/   level[2], clase165);  //   Cali, Presupuesto
      Classification clase167 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_FIN_PLANEACION_PPTAL        , "01",  /* 04030101*/ level[3], clase166);  //   Cali, Planeacion Presupuestal
      Classification clase168 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_FIN_EJECUCION_PPTAL         , "02",  /* 04030102*/ level[3], clase166);  //   Cali, Ejecucion Presupuestal
      Classification clase169 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_TESORERIA                         , "02",  /* 040302*/   level[2], clase165);  //   Cali, Tesoreria
      Classification clase170 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_FIN_PAGADURIA               , "01",  /* 04030201*/ level[3], clase169);  //   Cali, Pagaduria
      Classification clase171 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_FIN_INVERSIONES             , "02",  /* 04030202*/ level[3], clase169);  //   Cali, Inversiones
      Classification clase172 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_CONTABILIDAD                      , "03",  /* 040303*/   level[2], clase165);  //   Cali, Contabilidad
      Classification clase173 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , "01",  /* 04030301*/ level[3], clase172);  //   Cali, Estados Financieros
      Classification clase174 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_FIN_LIBROS_CONTABLES        , "02",  /* 04030302*/ level[3], clase172);  //   Cali, Libros contables
      Classification clase175 = createClass( tenant, classificationRootPath,   Constant.TITLE_CAL_OFICINA_PERSONAL                          , "04",  /* 0404*/     level[1], clase145);  //   Cali, Subgerencia de Personal
      Classification clase176 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_HOJAS_DE_VIDA                     , "01",  /* 040401*/   level[2], clase175);  //   Cali, Hojas de vida
      Classification clase177 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_PER_CANDIDATOS              , "01",  /* 04040101*/ level[3], clase176);  //   Cali, Candidatos de personal
      Classification clase178 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_PER_PERSONAL_ACTIVO         , "02",  /* 04040102*/ level[3], clase176);  //   Cali, Personal activo
      Classification clase179 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_PER_PENSIONADOS             , "03",  /* 04040103*/ level[3], clase176);  //   Cali, Pensionados
      Classification clase180 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_SANCIONES                         , "02",  /* 040402*/   level[2], clase175);  //   Cali, Sanciones de personal
      Classification clase181 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_PER_INVESTIGACIONES         , "01",  /* 04040201*/ level[3], clase180);  //   Cali, Investigaciones disciplinarias
      Classification clase182 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_PER_FALLOS_DE_PERSONAL      , "02",  /* 04040202*/ level[3], clase180);  //   Cali, Fallos de personal
      Classification clase183 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_EVALUACIONES                      , "03",  /* 040403*/   level[2], clase175);  //   Cali, Evaluaciones de personal
      Classification clase184 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_PER_DESEMPENO               , "01",  /* 04040301*/ level[3], clase183);  //   Cali, Evaluaciones de desempeeo
      Classification clase185 = createClass( tenant, classificationRootPath,   Constant.TITLE_CAL_OFICINA_JURIDICA                          , "05",  /* 0405*/     level[1], clase145);  //   Cali, Subgerencia Juridica
      Classification clase186 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_PROCESOS                          , "01",  /* 040501*/   level[2], clase185);  //   Cali, Procesos juridicos
      Classification clase187 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_JUR_DEMANDAS                , "01",  /* 04050101*/ level[3], clase186);  //   Cali, Demandas en curso
      Classification clase188 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_JUR_FALLOS_JUDICIALES       , "02",  /* 04050102*/ level[3], clase186);  //   Cali, Demandas en curso
      Classification clase189 = createClass( tenant, classificationRootPath,   Constant.TITLE_CAL_OFICINA_ADMINISTRACION                    , "06",  /* 0406*/     level[1], clase145);  //   Cali, Subgerencia Administrativa
      Classification clase190 = createClass( tenant, classificationRootPath,     Constant.TITLE_CAL_SERIE_ACTIVOS_FIJOS                     , "01",  /* 040601*/   level[2], clase189);  //   Cali, Activos fijos
      Classification clase191 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_ADM_EDIFICACIONES           , "01",  /* 04060101*/ level[3], clase190);  //   Cali, Edificaciones
      Classification clase192 = createClass( tenant, classificationRootPath,        Constant.TITLE_CAL_SUBSERIE_ADM_SERVICIOS               , "02",  /* 04060102*/ level[3], clase190);  //   Cali, Servicios publicos

      Classification clase193 = createClass( tenant, classificationRootPath, Constant.TITLE_SEDE_BARRANQUILLA                               , "05",  /* 05*/       level[0], null);      //   Sede Barranquilla
      Classification clase194 = createClass( tenant, classificationRootPath,   Constant.TITLE_BAQ_OFICINA_SUBGERENCIA                       , "01",  /* 0501*/     level[1], clase193);  //   Gerencia Barranquilla
      Classification clase195 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_ACTAS                             , "01",  /* 050101*/   level[2], clase194);  //   Barranquilla, Actas
      Classification clase196 = createClass( tenant, classificationRootPath,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , "01",  /* 05010101*/ level[3], clase195);  //   Barranquilla, Actas_junta_directiva
      Classification clase197 = createClass( tenant, classificationRootPath,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_GERENCIA        , "02",  /* 05010102*/ level[3], clase195);  //   Barranquilla, Actas_comite_gerencia
      Classification clase198 = createClass( tenant, classificationRootPath,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_FINANCIERO      , "03",  /* 05010103*/ level[3], clase195);  //   Barranquilla, Actas_comite_financiero
      Classification clase199 = createClass( tenant, classificationRootPath,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , "04",  /* 05010104*/ level[3], clase195);  //   Barranquilla, Actas_comite_administrativo
      Classification clase200 = createClass( tenant, classificationRootPath,       Constant.TITLE_BAQ_SUBSERIE_ACTAS_COMITE_OPERACIONES     , "05",  /* 05010105*/ level[3], clase195);  //   Barranquilla, Actas_comite_operaciones
      Classification clase202 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_PLANES                            , "02",  /* 050102*/   level[2], clase194);  //   Barranquilla, Planes
      Classification clase203 = createClass( tenant, classificationRootPath,       Constant.TITLE_BAQ_SUBSERIE_PLAN_OPERATIVO               , "01",  /* 05010201*/ level[3], clase202);  //   Barranquilla, Plan_operativo
      Classification clase204 = createClass( tenant, classificationRootPath,       Constant.TITLE_BAQ_SUBSERIE_PLAN_FINANCIERO              , "02",  /* 05010202*/ level[3], clase202);  //   Barranquilla, Plan_financiero
      Classification clase205 = createClass( tenant, classificationRootPath,       Constant.TITLE_BAQ_SUBSERIE_PRESUPUESTO                  , "03",  /* 05010203*/ level[3], clase202);  //   Barranquilla, Presupuesto
      Classification clase206 = createClass( tenant, classificationRootPath,   Constant.TITLE_BAQ_OFICINA_OPERACIONES                       , "02",  /* 0502*/     level[1], clase193);  //   Barranquilla, Subgerencia de Operaciones
      Classification clase207 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_ACTAS_OPERACIONES                 , "01",  /* 050201*/   level[2], clase206);  //   Barranquilla, Actas Operaciones
      Classification clase208 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , "01",  /* 05020101*/ level[3], clase207);  //   Barranquilla, Actas Comite calidad
      Classification clase209 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , "02",  /* 05020102*/ level[3], clase207);  //   Barranquilla, Actas Comite planeacion
      Classification clase210 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_CONTRATOS                         , "02",  /* 050202*/   level[2], clase206);  //   Barranquilla, Contratos
      Classification clase211 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_OPER_CONTRATOS_OPERACION    , "01",  /* 05020201*/ level[3], clase210);  //   Barranquilla, Contratos de Operacion
      Classification clase212 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_OPER_CONTRATOS_INVERSION    , "02",  /* 05020202*/ level[3], clase210);  //   Barranquilla, Contratos de Inversion
      Classification clase213 = createClass( tenant, classificationRootPath,   Constant.TITLE_BAQ_OFICINA_FINANCIERA                        , "03",  /* 0503*/     level[1], clase193);  //   Barranquilla, Subgerencia Financiera
      Classification clase214 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_PRESUPUESTO                       , "01",  /* 050301*/   level[2], clase213);  //   Barranquilla, Presupuesto
      Classification clase215 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_FIN_PLANEACION_PPTAL        , "01",  /* 05030101*/ level[3], clase214);  //   Barranquilla, Planeacion Presupuestal
      Classification clase216 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_FIN_EJECUCION_PPTAL         , "02",  /* 05030102*/ level[3], clase214);  //   Barranquilla, Ejecucion Presupuestal
      Classification clase217 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_TESORERIA                         , "02",  /* 050302*/   level[2], clase213);  //   Barranquilla, Tesoreria
      Classification clase218 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_FIN_PAGADURIA               , "01",  /* 05030201*/ level[3], clase217);  //   Barranquilla, Pagaduria
      Classification clase219 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_FIN_INVERSIONES             , "02",  /* 05030202*/ level[3], clase217);  //   Barranquilla, Inversiones
      Classification clase220 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_CONTABILIDAD                      , "03",  /* 050303*/   level[2], clase213);  //   Barranquilla, Contabilidad
      Classification clase221 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , "01",  /* 05030301*/ level[3], clase220);  //   Barranquilla, Estados Financieros
      Classification clase222 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_FIN_LIBROS_CONTABLES        , "02",  /* 05030302*/ level[3], clase220);  //   Barranquilla, Libros contables
      Classification clase223 = createClass( tenant, classificationRootPath,   Constant.TITLE_BAQ_OFICINA_PERSONAL                          , "04",  /* 0504*/     level[1], clase193);  //   Barranquilla, Subgerencia de Personal
      Classification clase224 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_HOJAS_DE_VIDA                     , "01",  /* 050401*/   level[2], clase223);  //   Barranquilla, Hojas de vida
      Classification clase225 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_PER_CANDIDATOS              , "01",  /* 05040101*/ level[3], clase224);  //   Barranquilla, Candidatos de personal
      Classification clase226 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_PER_PERSONAL_ACTIVO         , "02",  /* 05040102*/ level[3], clase224);  //   Barranquilla, Personal activo
      Classification clase227 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_PER_PENSIONADOS             , "03",  /* 05040103*/ level[3], clase224);  //   Barranquilla, Pensionados
      Classification clase228 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_SANCIONES                         , "02",  /* 050402*/   level[2], clase223);  //   Barranquilla, Sanciones de personal
      Classification clase229 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_PER_INVESTIGACIONES         , "01",  /* 05040201*/ level[3], clase228);  //   Barranquilla, Investigaciones disciplinarias
      Classification clase230 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_PER_FALLOS_DE_PERSONAL      , "02",  /* 05040202*/ level[3], clase228);  //   Barranquilla, Fallos de personal
      Classification clase231 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_EVALUACIONES                      , "03",  /* 050403*/   level[2], clase223);  //   Barranquilla, Evaluaciones de personal
      Classification clase232 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_PER_DESEMPENO               , "01",  /* 05040301*/ level[3], clase231);  //   Barranquilla, Evaluaciones de desempeeo
      Classification clase233 = createClass( tenant, classificationRootPath,   Constant.TITLE_BAQ_OFICINA_JURIDICA                          , "05",  /* 0505*/     level[1], clase193);  //   Barranquilla, Subgerencia Juridica
      Classification clase234 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_PROCESOS                          , "01",  /* 050501*/   level[2], clase233);  //   Barranquilla, Procesos juridicos
      Classification clase235 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_JUR_DEMANDAS                , "01",  /* 05050101*/ level[3], clase234);  //   Barranquilla, Demandas en curso
      Classification clase236 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_JUR_FALLOS_JUDICIALES       , "02",  /* 05050102*/ level[3], clase234);  //   Barranquilla, Demandas en curso
      Classification clase237 = createClass( tenant, classificationRootPath,   Constant.TITLE_BAQ_OFICINA_ADMINISTRACION                    , "06",  /* 0506*/     level[1], clase193);  //   Barranquilla, Subgerencia Administrativa
      Classification clase238 = createClass( tenant, classificationRootPath,     Constant.TITLE_BAQ_SERIE_ACTIVOS_FIJOS                     , "01",  /* 050601*/   level[2], clase237);  //   Barranquilla, Activos fijos
      Classification clase239 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_ADM_EDIFICACIONES           , "01",  /* 05060101*/ level[3], clase238);  //   Barranquilla, Edificaciones
      Classification clase240 = createClass( tenant, classificationRootPath,        Constant.TITLE_BAQ_SUBSERIE_ADM_SERVICIOS               , "02",  /* 05060102*/ level[3], clase238);  //   Barranquilla, Servicios publicos

      Classification clase241 = createClass( tenant, classificationRootPath, Constant.TITLE_SEDE_BUCARAMANGA                                , "06",  /* 06*/       level[3], null);      //   Sede Bucaramanga
      Classification clase242 = createClass( tenant, classificationRootPath,   Constant.TITLE_BUC_OFICINA_SUBGERENCIA                       , "01",  /* 0601*/     level[1], clase241);  //   Gerencia Bucaramanga
      Classification clase243 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_ACTAS                             , "01",  /* 060101*/   level[2], clase242);  //   Bucaramanga, Actas
      Classification clase244 = createClass( tenant, classificationRootPath,       Constant.TITLE_BUC_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , "01",  /* 06010101*/ level[3], clase243);  //   Bucaramanga, Actas_junta_directiva
      Classification clase245 = createClass( tenant, classificationRootPath,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_GERENCIA        , "02",  /* 06010102*/ level[3], clase243);  //   Bucaramanga, Actas_comite_gerencia
      Classification clase246 = createClass( tenant, classificationRootPath,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_FINANCIERO      , "03",  /* 06010103*/ level[3], clase243);  //   Bucaramanga, Actas_comite_financiero
      Classification clase247 = createClass( tenant, classificationRootPath,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , "04",  /* 06010104*/ level[3], clase243);  //   Bucaramanga, Actas_comite_administrativo
      Classification clase248 = createClass( tenant, classificationRootPath,       Constant.TITLE_BUC_SUBSERIE_ACTAS_COMITE_OPERACIONES     , "05",  /* 06010105*/ level[3], clase243);  //   Bucaramanga, Actas_comite_operaciones
      Classification clase250 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_PLANES                            , "02",  /* 060102*/   level[2], clase242);  //   Bucaramanga, Planes
      Classification clase251 = createClass( tenant, classificationRootPath,       Constant.TITLE_BUC_SUBSERIE_PLAN_OPERATIVO               , "01",  /* 06010201*/ level[3], clase250);  //   Bucaramanga, Plan_operativo
      Classification clase252 = createClass( tenant, classificationRootPath,       Constant.TITLE_BUC_SUBSERIE_PLAN_FINANCIERO              , "02",  /* 06010202*/ level[3], clase250);  //   Bucaramanga, Plan_financiero
      Classification clase253 = createClass( tenant, classificationRootPath,       Constant.TITLE_BUC_SUBSERIE_PRESUPUESTO                  , "03",  /* 06010203*/ level[3], clase250);  //   Bucaramanga, Presupuesto
      Classification clase254 = createClass( tenant, classificationRootPath,   Constant.TITLE_BUC_OFICINA_OPERACIONES                       , "02",  /* 0602*/     level[1], clase241);  //   Bucaramanga, Subgerencia de Operaciones
      Classification clase255 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_ACTAS_OPERACIONES                 , "01",  /* 060201*/   level[2], clase254);  //   Bucaramanga, Actas Operaciones
      Classification clase256 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , "01",  /* 06020101*/ level[3], clase255);  //   Bucaramanga, Actas Comite calidad
      Classification clase257 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , "02",  /* 06020102*/ level[3], clase255);  //   Bucaramanga, Actas Comite planeacion
      Classification clase258 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_CONTRATOS                         , "02",  /* 060202*/   level[2], clase254);  //   Bucaramanga, Contratos
      Classification clase259 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_OPER_CONTRATOS_OPERACION    , "01",  /* 06020201*/ level[3], clase258);  //   Bucaramanga, Contratos de Operacion
      Classification clase260 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_OPER_CONTRATOS_INVERSION    , "02",  /* 06020202*/ level[3], clase258);  //   Bucaramanga, Contratos de Inversion
      Classification clase261 = createClass( tenant, classificationRootPath,   Constant.TITLE_BUC_OFICINA_FINANCIERA                        , "03",  /* 0603*/     level[1], clase241);  //   Bucaramanga, Subgerencia Financiera
      Classification clase262 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_PRESUPUESTO                       , "01",  /* 060301*/   level[2], clase261);  //   Bucaramanga, Presupuesto
      Classification clase263 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_FIN_PLANEACION_PPTAL        , "01",  /* 06030101*/ level[3], clase262);  //   Bucaramanga, Planeacion Presupuestal
      Classification clase264 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_FIN_EJECUCION_PPTAL         , "02",  /* 06030102*/ level[3], clase262);  //   Bucaramanga, Ejecucion Presupuestal
      Classification clase265 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_TESORERIA                         , "02",  /* 060302*/   level[2], clase261);  //   Bucaramanga, Tesoreria
      Classification clase266 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_FIN_PAGADURIA               , "01",  /* 06030201*/ level[3], clase265);  //   Bucaramanga, Pagaduria
      Classification clase267 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_FIN_INVERSIONES             , "02",  /* 06030202*/ level[3], clase265);  //   Bucaramanga, Inversiones
      Classification clase268 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_CONTABILIDAD                      , "03",  /* 060303*/   level[2], clase261);  //   Bucaramanga, Contabilidad
      Classification clase269 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , "01",  /* 06030301*/ level[3], clase268);  //   Bucaramanga, Estados Financieros
      Classification clase270 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_FIN_LIBROS_CONTABLES        , "02",  /* 06030302*/ level[3], clase268);  //   Bucaramanga, Libros contables
      Classification clase271 = createClass( tenant, classificationRootPath,   Constant.TITLE_BUC_OFICINA_PERSONAL                          , "04",  /* 0604*/     level[1], clase241);  //   Bucaramanga, Subgerencia de Personal
      Classification clase272 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_HOJAS_DE_VIDA                     , "01",  /* 060401*/   level[2], clase271);  //   Bucaramanga, Hojas de vida
      Classification clase273 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_PER_CANDIDATOS              , "01",  /* 06040101*/ level[3], clase272);  //   Bucaramanga, Candidatos de personal
      Classification clase274 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_PER_PERSONAL_ACTIVO         , "02",  /* 06040102*/ level[3], clase272);  //   Bucaramanga, Personal activo
      Classification clase275 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_PER_PENSIONADOS             , "03",  /* 06040103*/ level[3], clase272);  //   Bucaramanga, Pensionados
      Classification clase276 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_SANCIONES                         , "02",  /* 060402*/   level[2], clase271);  //   Bucaramanga, Sanciones de personal
      Classification clase277 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_PER_INVESTIGACIONES         , "01",  /* 06040201*/ level[3], clase276);  //   Bucaramanga, Investigaciones disciplinarias
      Classification clase278 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_PER_FALLOS_DE_PERSONAL      , "02",  /* 06040202*/ level[3], clase276);  //   Bucaramanga, Fallos de personal
      Classification clase279 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_EVALUACIONES                      , "03",  /* 060403*/   level[2], clase271);  //   Bucaramanga, Evaluaciones de personal
      Classification clase280 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_PER_DESEMPENO               , "01",  /* 06040301*/ level[3], clase279);  //   Bucaramanga, Evaluaciones de desempeeo
      Classification clase281 = createClass( tenant, classificationRootPath,   Constant.TITLE_BUC_OFICINA_JURIDICA                          , "05",  /* 0605*/     level[1], clase241);  //   Bucaramanga, Subgerencia Juridica
      Classification clase282 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_PROCESOS                          , "01",  /* 060501*/   level[2], clase281);  //   Bucaramanga, Procesos juridicos
      Classification clase283 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_JUR_DEMANDAS                , "01",  /* 06050101*/ level[3], clase282);  //   Bucaramanga, Demandas en curso
      Classification clase284 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_JUR_FALLOS_JUDICIALES       , "02",  /* 06050102*/ level[3], clase282);  //   Bucaramanga, Demandas en curso
      Classification clase285 = createClass( tenant, classificationRootPath,   Constant.TITLE_BUC_OFICINA_ADMINISTRACION                    , "06",  /* 0606*/     level[1], clase241);  //   Bucaramanga, Subgerencia Administrativa
      Classification clase286 = createClass( tenant, classificationRootPath,     Constant.TITLE_BUC_SERIE_ACTIVOS_FIJOS                     , "01",  /* 060601*/   level[2], clase285);  //   Bucaramanga, Activos fijos
      Classification clase287 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_ADM_EDIFICACIONES           , "01",  /* 06060101*/ level[3], clase286);  //   Bucaramanga, Edificaciones
      Classification clase288 = createClass( tenant, classificationRootPath,        Constant.TITLE_BUC_SUBSERIE_ADM_SERVICIOS               , "02",  /* 06060102*/ level[3], clase286);  //   Bucaramanga, Servicios publicos

      Classification clase289 = createClass( tenant, classificationRootPath, Constant.TITLE_SEDE_CARTAGENA                                  , "07",  /* 07*/       level[3], null);      //   Sede Cartagena
      Classification clase290 = createClass( tenant, classificationRootPath,   Constant.TITLE_CTG_OFICINA_SUBGERENCIA                       , "01",  /* 0701*/     level[1], clase289);  //   Gerencia Cartagena
      Classification clase291 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE__ACTAS                            , "01",  /* 070101*/   level[2], clase290);  //   Cartagena, Actas
      Classification clase292 = createClass( tenant, classificationRootPath,       Constant.TITLE_CTG_SUBSERIE_ACTAS_JUNTA_DIRECTIVA        , "01",  /* 07010101*/ level[3], clase291);  //   Cartagena, Actas_junta_directiva
      Classification clase293 = createClass( tenant, classificationRootPath,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_GERENCIA        , "02",  /* 07010102*/ level[3], clase291);  //   Cartagena, Actas_comite_gerencia
      Classification clase294 = createClass( tenant, classificationRootPath,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_FINANCIERO      , "03",  /* 07010103*/ level[3], clase291);  //   Cartagena, Actas_comite_financiero
      Classification clase295 = createClass( tenant, classificationRootPath,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_ADMINISTRATIVO  , "04",  /* 07010104*/ level[3], clase291);  //   Cartagena, Actas_comite_administrativo
      Classification clase296 = createClass( tenant, classificationRootPath,       Constant.TITLE_CTG_SUBSERIE_ACTAS_COMITE_OPERACIONES     , "05",  /* 07010105*/ level[3], clase291);  //   Cartagena, Actas_comite_operaciones
      Classification clase298 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_PLANES                            , "02",  /* 070102*/   level[2], clase290);  //   Cartagena, Planes
      Classification clase299 = createClass( tenant, classificationRootPath,       Constant.TITLE_CTG_SUBSERIE_PLAN_OPERATIVO               , "01",  /* 07010201*/ level[3], clase298);  //   Cartagena, Plan_operativo
      Classification clase300 = createClass( tenant, classificationRootPath,       Constant.TITLE_CTG_SUBSERIE_PLAN_FINANCIERO              , "02",  /* 07010202*/ level[3], clase298);  //   Cartagena, Plan_financiero
      Classification clase301 = createClass( tenant, classificationRootPath,       Constant.TITLE_CTG_SUBSERIE_PRESUPUESTO                  , "03",  /* 07010203*/ level[3], clase298);  //   Cartagena, Presupuesto
      Classification clase302 = createClass( tenant, classificationRootPath,   Constant.TITLE_CTG_OFICINA_OPERACIONES                       , "02",  /* 0702*/     level[1], clase289);  //   Cartagena, Subgerencia de Operaciones
      Classification clase303 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_ACTAS_OPERACIONES                 , "01",  /* 070201*/   level[2], clase302);  //   Cartagena, Actas Operaciones
      Classification clase304 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_OPER_CTAS_COMITE_CALIDAD    , "01",  /* 07020101*/ level[3], clase303);  //   Cartagena, Actas Comite calidad
      Classification clase305 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_OPER_CTAS_COMITE_PLANEACION , "02",  /* 07020102*/ level[3], clase303);  //   Cartagena, Actas Comite planeacion
      Classification clase306 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_CONTRATOS                         , "02",  /* 070202*/   level[2], clase302);  //   Cartagena, Contratos
      Classification clase307 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_OPER_CONTRATOS_OPERACION    , "01",  /* 07020201*/ level[3], clase306);  //   Cartagena, Contratos de Operacion
      Classification clase308 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_OPER_CONTRATOS_INVERSION    , "02",  /* 07020202*/ level[3], clase306);  //   Cartagena, Contratos de Inversion
      Classification clase309 = createClass( tenant, classificationRootPath,   Constant.TITLE_CTG_OFICINA_FINANCIERA                        , "03",  /* 0703*/     level[1], clase289);  //   Cartagena, Subgerencia Financiera
      Classification clase310 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_PRESUPUESTO                       , "01",  /* 070301*/   level[2], clase309);  //   Cartagena, Presupuesto
      Classification clase311 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_FIN_PLANEACION_PPTAL        , "01",  /* 07030101*/ level[3], clase310);  //   Cartagena, Planeacion Presupuestal
      Classification clase312 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_FIN_EJECUCION_PPTAL         , "02",  /* 07030102*/ level[3], clase310);  //   Cartagena, Ejecucion Presupuestal
      Classification clase313 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_TESORERIA                         , "02",  /* 070302*/   level[2], clase309);  //   Cartagena, Tesoreria
      Classification clase314 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_FIN_PAGADURIA               , "01",  /* 07030201*/ level[3], clase313);  //   Cartagena, Pagaduria
      Classification clase315 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_FIN_INVERSIONES             , "02",  /* 07030202*/ level[3], clase313);  //   Cartagena, Inversiones
      Classification clase316 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_CONTABILIDAD                      , "03",  /* 070303*/   level[2], clase309);  //   Cartagena, Contabilidad
      Classification clase317 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_FIN_ESTADOS_FINANCIEROS     , "01",  /* 07030301*/ level[3], clase316);  //   Cartagena, Estados Financieros
      Classification clase318 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_FIN_LIBROS_CONTABLES        , "02",  /* 07030302*/ level[3], clase316);  //   Cartagena, Libros contables
      Classification clase319 = createClass( tenant, classificationRootPath,   Constant.TITLE_CTG_OFICINA_PERSONAL                          , "04",  /* 0704*/     level[1], clase289);  //   Cartagena, Subgerencia de Personal
      Classification clase320 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_HOJAS_DE_VIDA                     , "01",  /* 070401*/   level[2], clase319);  //   Cartagena, Hojas de vida
      Classification clase321 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_PER_CANDIDATOS              , "01",  /* 07040101*/ level[3], clase320);  //   Cartagena, Candidatos de personal
      Classification clase322 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_PER_PERSONAL_ACTIVO         , "02",  /* 07040102*/ level[3], clase320);  //   Cartagena, Personal activo
      Classification clase323 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_PER_PENSIONADOS             , "03",  /* 07040103*/ level[3], clase320);  //   Cartagena, Pensionados
      Classification clase324 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_SANCIONES                         , "02",  /* 070402*/   level[2], clase319);  //   Cartagena, Sanciones de personal
      Classification clase325 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_PER_INVESTIGACIONES         , "01",  /* 07040201*/ level[3], clase324);  //   Cartagena, Investigaciones disciplinarias
      Classification clase326 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_PER_FALLOS_DE_PERSONAL      , "02",  /* 07040202*/ level[3], clase324);  //   Cartagena, Fallos de personal
      Classification clase327 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_EVALUACIONES                      , "03",  /* 070403*/   level[2], clase319);  //   Cartagena, Evaluaciones de personal
      Classification clase328 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_PER_DESEMPENO               , "01",  /* 07040301*/ level[3], clase327);  //   Cartagena, Evaluaciones de desempeeo
      Classification clase329 = createClass( tenant, classificationRootPath,   Constant.TITLE_CTG_OFICINA_JURIDICA                          , "05",  /* 0705*/     level[1], clase289);  //   Cartagena, Subgerencia Juridica
      Classification clase330 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_PROCESOS                          , "01",  /* 070501*/   level[2], clase329);  //   Cartagena, Procesos juridicos
      Classification clase331 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_JUR_DEMANDAS                , "01",  /* 07050101*/ level[3], clase330);  //   Cartagena, Demandas en curso
      Classification clase332 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_JUR_FALLOS_JUDICIALES       , "02",  /* 07050102*/ level[3], clase330);  //   Cartagena, Demandas en curso
      Classification clase333 = createClass( tenant, classificationRootPath,   Constant.TITLE_CTG_OFICINA_ADMINISTRACION                    , "06",  /* 0706*/     level[1], clase289);  //   Cartagena, Subgerencia Administrativa
      Classification clase334 = createClass( tenant, classificationRootPath,     Constant.TITLE_CTG_SERIE_ACTIVOS_FIJOS                     , "01",  /* 070601*/   level[2], clase333);  //   Cartagena, Activos fijos
      Classification clase335 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_ADM_EDIFICACIONES           , "01",  /* 07060101*/ level[3], clase334);  //   Cartagena, Edificaciones
      Classification clase336 = createClass( tenant, classificationRootPath,        Constant.TITLE_CTG_SUBSERIE_ADM_SERVICIOS               , "02",  /* 07060102*/ level[3], clase334);  //   Cartagena, Servicios publicos
      getLogger().info("    >>> "+ nClasses+ " classes created");
      printSequenceStats();

   }//registerClasses


   private void   printSequenceStats()
   {
      getLogger().info("    >>> Secuences created ["+ nSequences+ "]" );
      StringBuilder line = new StringBuilder();
      int i = 0;
      for (String num: seqNumbers)
      {
         if ( (i % 4 == 3))
         {  getLogger().info("       "+ line);
            line.setLength(0);
         }else if( (i % 3 != 0))
         { line.append(", ");
         }
         line.append( num);
         i++;
      }
      if (i > 0)
         getLogger().info("       "+ line);

   }//printSequenceStats


   private String initJcrClassification(Tenant tenant, String classificationCode)
         throws RepositoryException, UnknownHostException
   {
      String classificationRootPath = tenant.getWorkspace()+ Parm.PATH_SEPARATOR+ classificationCode;
      Node      classificationRoot  = Repo.getInstance().addNode(classificationRootPath, "CLASSIFICATION_ROOT", currentUser);
      if (!classificationRootPath.equals(classificationRoot.getPath()))
      {   throw new IllegalStateException("No pudo crear raíz del esquema de clasificación en JCR");
      }
      return classificationRootPath;

   }//initJcrClassification


   private Classification createClass( Tenant tenant, String classificationRootPath,  String name, String classCode, Level level, Classification parent)
   {
      Classification classificationClass = new Classification( tenant, level, name, classCode, parent, new ObjectToProtect());

      classificationClass.setTenant(tenant);
      Schema schema = null;

      Level nivel = classificationClass.getLevel();
      if ( !nivel.isPersisted())
      {
         Level newLevel = levelRepository.findByLevel(nivel.getOrden());
         if( newLevel == null || !nivel.getOrden().equals(newLevel.getOrden()))
         {
            nivel.setTenant(tenant);
            schema = nivel.getSchema();
            if ( !schema.isPersisted())
            {  schema.setTenant(tenant);
               schemaRepository.saveAndFlush(schema);
            }
            levelRepository.saveAndFlush(nivel);
         }else
         {  classificationClass.setLevel(newLevel);
            schema = newLevel.getSchema();
         }
      }else
      {  schema = nivel.getSchema();
      }
      SchemaValues values = new SchemaValues(schema, null);
      values.setTenant(tenant);
      classificationClass.setMetadata(values);
      claseRepository.saveAndFlush(classificationClass);
      saveJCRClassification( classificationClass, classificationRootPath);
      nClasses++;
      if (classificationClass.isRoot())
         createSequence(tenant, classificationClass);

      return classificationClass;

   }//createClass


   private void saveJCRClassification( Classification classificationClass, String classificationRootPath)
   {
      try
      {
         Classification parent  = classificationClass.getOwner();
         String parentPath      = parent ==  null? classificationRootPath: parent.getPath();
         String childCode       = classificationClass.getClassCode();
         String childName       = classificationClass.getName();
         String childLevel      = ""+ classificationClass.getLevel().getOrden();
         String namespace       = classificationClass.getTenant().getName()+ ":";
         Node classificationJCR = addJCRClassification( namespace, parentPath, childCode, childName, childLevel);
         updateJCRClassification(namespace, classificationJCR, classificationClass);
         Repo.getInstance().save();
      } catch(Exception e)
      { throw new IllegalStateException("*** No pudo guardar estructura de clasificación en el repositorio. Razón\n"+ e.getLocalizedMessage());
      }

   }//saveJCRClassification


   private Node addJCRClassification(String namespace, String parentPath, String childCode, String childName, String childLevel)
         throws RepositoryException, UnknownHostException
   {
      String childPath = parentPath+ Parm.PATH_SEPARATOR+ childCode;
      Node   child     = Repo.getInstance().addNode(childPath, childName, currentUser);
      child.setProperty( "jcr:nodeTypeName", NodeType.CLASSIFICATION.name());
      child.addMixin   ( "mix:referenceable");
      child.setProperty( namespace+ "code",  childCode);     // Subclass code inside the parent class  vg 01, 02, etc
      child.setProperty( namespace+ "level", childLevel);
      return child;
   }//addJCRClassification


   private void updateJCRClassification(String namespace, Node classificationJCR, Classification classificationClass)
   {
      try
      {
         classificationJCR.setProperty( namespace+ "tenant"           , classificationClass.getTenant().getId());
         classificationJCR.setProperty( namespace+ "retentionSchedule", classificationClass.getRetentionSchedule().getId());
         classificationJCR.setProperty( namespace+ "classCode"        , classificationClass.formatCode()); // Complete class code vg 01-01-01, 01-01-02, etc
         boolean isOpen   = classificationClass.isOpen();
         classificationJCR.setProperty   ( namespace+ "open"      , isOpen);
         if (isOpen)
         {  classificationJCR.setProperty( namespace+ "dateOpened", TextUtil.formatDate(classificationClass.getDateOpened()));
         }  else
         {  classificationJCR.setProperty( namespace+ "dateClosed", TextUtil.formatDate(classificationClass.getDateClosed()));
         }
         SchemaValues metadata = classificationClass.getMetadata();
         Repo.getInstance().updateMixin( classificationJCR, namespace, metadata.getSchema(), metadata );
         Repo.getInstance().save();    // TODO: Revisar si funciona para multiusuario, o si toca tener una sesión para cada usuario (guardada en la vaadin session)
      } catch(Exception e)
      {   throw new IllegalStateException("No pudo actualizar clase["+ classificationClass.formatCode()+ "]. Razón\n"+ e.getMessage());
      }

   }//updateJCRClassification


   private synchronized void createSequence(Tenant tenant, Classification classificationClass)
   {
      String classCode = classificationClass.getCode();
      String rootClass = TextUtil.pad(classCode.substring( classCode.lastIndexOf(Parm.PATH_SEPARATOR)+1), 3);
      String prefix    = rootClass+ "-"+ LocalDateTime.now().getYear();;
      String suffix    = "E";
      String seqName   = prefix+ "-"+ suffix;
      if ( !numerator.sequenceExists(tenant, seqName, prefix, suffix) )
      {
         seqNumbers.add( Numerator.sequenceName(tenant, seqName, prefix, suffix));
         nSequences++;
         numerator.getSequence(tenant, seqName, prefix, suffix, 0L/*initial*/, 1/*increment*/, 5/*length*/);
      }
   }//createSequence


}//ClassificationGenerator
