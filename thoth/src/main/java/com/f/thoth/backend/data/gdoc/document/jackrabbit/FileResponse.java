package com.f.thoth.backend.data.gdoc.document.jackrabbit;

public class FileResponse
{
    private byte[] bytes;
    private String contentType;

    public byte[]  getBytes() { return bytes; }
    public void    setBytes(byte[] bytes) { this.bytes = bytes; }

    public String  getContentType() { return contentType; }
    public void    setContentType(String contentType) { this.contentType = contentType; }

}//FileResponse
