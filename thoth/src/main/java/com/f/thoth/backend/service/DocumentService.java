package com.f.thoth.backend.service;

import org.springframework.stereotype.Service;

@Service
//public class DocumentService implements FilterableCrudService<Document>, PermissionService<Document>
public class DocumentService 
{
/*
  Para crear el fileNode del documento en el repositorio jcr
Node fileNodeParent = session.getNode("pathToParentNode"); // /node1/node2/
Node fileNode = fileNodeParent.addNode("theFile", "nt:file");
Node content = fileNode.addNode("jcr:content", "nt:resource");
InputStream is = getFileInputStream();//Get the file data as stream.
Binary binary = session.getValueFactory().createBinary(is);
content.setProperty("jcr:data", binary);
session.save();

// To enable versioning use VersionManager
VersionManager vm = session.getWorkspace().getVersionManager();
vm.checkin(fileNode.getPath());

 */
}//DocumentService
