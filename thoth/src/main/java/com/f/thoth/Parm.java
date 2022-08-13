package com.f.thoth;

import java.time.LocalDateTime;

public class Parm
{
   
   public static final boolean       IN_MEMORY_JCR_REPO = true;                        // true: Use an in-memory repo / false: Use a disk based repo
   public static final String         DEFAULT_REPO_HOST = "127.0.0.1";                 // IP for the REPO
   public static final int            DEFAULT_REPO_PORT = 27017;                       // Port for the REPO
   public static final String         DEFAULT_REPO_NAME = "thoth";                     // Default document REPO name
   public static final String       DEFAULT_ADMIN_LOGIN = "admin";                     // Default id for Toth administrator
   public static final String    DEFAULT_ADMIN_PASSWORD = "admin";                     // Password of the default Toth administrator
   public static final String            PATH_SEPARATOR = "/";                         // Default separator for components of the class hierarchy
   public static final String            CODE_SEPARATOR = ":";                         // Separator between volume code and instance code
   public static final int              CLASS_CODE_SIZE = 2;                           // Class code size

   public static final String                   TENANT  = "TENANT";                    // Attribute name for Vaadin session attribute TENANT and DB table of Tenants    
   public static final String             CURRENT_USER  = "CURRENT_USER";              // Attribute name for Vaadin session attribute CURRENT_USER 
   public static final String               CLASS_ROOT  = "CLASS_ROOT";                // Attribute name for the root of the classification tree for current tenant 
                                                                                       //                in order to simulate multiple tenants in a Repository
   public static final String          EXPEDIENTE_ROOT  = "EXPEDIENTE_ROOT";           // Attribute name for the root of the expediente tree for the current tenant
   
   public static final Integer            MIN_CATEGORY  = 0;                           // Minimum possible security category of any information object (Doc, class, expediente...)
   public static final Integer            MAX_CATEGORY  = 5;                           // Maximum possible security category of any information object 
   public static final Integer        DEFAULT_CATEGORY  = MIN_CATEGORY;                // Default security category if not explicitly provided (i.e. object is public)
   public static final Integer          ADMIN_CATEGORY  = MAX_CATEGORY - 1;            // Maximum security category accessible for the administrator user

   public static final String          VALUE_SEPARATOR  = ";";                         // Separator  of values in a text value list, (vg a metadata ENUM type)
   public static final String               NULL_VALUE  = "*";                         // Null value for any metadata text value of the system

   public static final LocalDateTime      END_OF_TIMES  = LocalDateTime.parse("9999-12-31T11:59:59.999");   // Maximum date handled by the system

   public static final int       NOTIFICATION_DURATION  = 10000;                      // Time duration of a notification
   //TODO:  After debugging colocar NOTIFICATION_DURATION EN 4000

}//Parm
