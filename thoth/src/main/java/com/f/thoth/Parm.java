package com.f.thoth;

import java.time.LocalDateTime;

public class Parm
{
   
   public static final boolean     IN_MEMORY_JCR_REPO = true;
   public static final String       DEFAULT_REPO_HOST = "127.0.0.1";
   public static final int          DEFAULT_REPO_PORT = 27017;
   public static final String       DEFAULT_REPO_NAME = "thoth";
   public static final String     DEFAULT_ADMIN_LOGIN = "admin";
   public static final String  DEFAULT_ADMIN_PASSWORD = "admin";
   public static final String          PATH_SEPARATOR = "/";
   public static final String          CODE_SEPARATOR = ":";
   public static final int            CLASS_CODE_SIZE = 2;

   public static final String                 TENANT  = "TENANT";
   public static final String           CURRENT_USER  = "CURRENT_USER";
   public static final String             CLASS_ROOT  = "CLASS_ROOT";
   public static final String        EXPEDIENTE_ROOT  = "EXPEDIENTE_ROOT";
   public static final Integer          MIN_CATEGORY  = 0;
   public static final Integer          MAX_CATEGORY  = 5;
   public static final Integer      DEFAULT_CATEGORY  = MIN_CATEGORY;
   public static final Integer        ADMIN_CATEGORY  = MAX_CATEGORY - 1;
   public static final String        VALUE_SEPARATOR  = ";";
   public static final LocalDateTime    END_OF_TIMES  = LocalDateTime.parse("5000-12-31T11:59:59");
   public static final String             NULL_VALUE  = "*";
   
}//Parm
