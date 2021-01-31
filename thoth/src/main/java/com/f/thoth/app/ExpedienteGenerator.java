package com.f.thoth.app;

import javax.jcr.Session;

import com.f.thoth.backend.data.security.Tenant;
import com.f.thoth.backend.repositories.ClassificationRepository;
import com.f.thoth.backend.repositories.ExpedienteRepository;

public class ExpedienteGenerator 
{
	private ClassificationRepository claseRepository;
	private ExpedienteRepository     expedienteRepository;
	private Session                  jcrSession;
	

   public ExpedienteGenerator( ClassificationRepository claseRepository, Session jcrSession, ExpedienteRepository expedienteRepository)
   {
	   this.claseRepository      = claseRepository;
	   this.expedienteRepository = expedienteRepository;
	   this.jcrSession           = jcrSession;
   }//ExpedienteGenerator constructor
   
   public void registerExpedientes( Tenant tenant)
   {
	   // 1. Para cada clase hoja CLASE
	   //    int numExpedientes =  random(1,10);
	   //    for ( int i= 0; i < numExpedientes; i++)
	   //        Genere expediente con clase CLASE, padre = null
	   //        Para cada expediente i
	   //           int depthExpedientes = random(1,4);
	   //           genereExpedientesHijos( depthExpedientes, expediente, CLASE)
	   //    endFor
	   
	   //    private void genereExpedientesHijos( int depth, Expediente Padre, Classification CLASE)
	   //       si depth = 0 
	   //           termine
	   //       Cree sub-Expediente currentExpediente con padre Padre, clase CLASE
	   //       if depth = 1 
	   //           volumen = random(0,1)
	   //       else
	   //          int numHijos = random(1,5)
	   //          for(int j= 0; j< numHijos; j++)
	   //              genereExpedientesHijos( depth-1, currentExpediente)
	   //          endFor
	   //       endif
	   //    endProc 
	   
	   
   }//registerExpedientes
   
}//ExpedienteGenerator
