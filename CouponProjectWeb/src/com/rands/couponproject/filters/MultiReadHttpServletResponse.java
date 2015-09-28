package com.rands.couponproject.filters;

/**
 * Copyright 2013 TouK
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Marek Maj <marekmaj2@gmail.com>
 */
public class MultiReadHttpServletResponse extends HttpServletResponseWrapper {

    private ServletOutputStream outputStream;
    private CachedServletOutputStream copiedOutput;
    private PrintWriter writer;

    public MultiReadHttpServletResponse(HttpServletResponse httpServletResponse) {
        super(httpServletResponse);
    }

    public String getContent() {
        if (copiedOutput != null) {
            try {
				return new String(copiedOutput.getCopy(), getCharacterEncoding());
			} catch (UnsupportedEncodingException e) {
				return new String(copiedOutput.getCopy());
			}
        }
        return null;
    }

    @Override
    public void flushBuffer() throws IOException {
        if (writer != null) {
            writer.flush();
        } else if (outputStream != null) {
            copiedOutput.flush();
        }
    }
    
    @Override
    public void reset() {
    	super.reset();
    	writer = null;
    	outputStream = null;
    }
    

    @Override
    public String getCharacterEncoding() {
        if (getResponse().getCharacterEncoding() == null){
            getResponse().setCharacterEncoding("UTF-8");
        }
        return getResponse().getCharacterEncoding();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null) {
            throw new IllegalStateException("getOutputStream() has already been called on this response.");
        }

        if (writer == null) {
            copiedOutput = new CachedServletOutputStream(getResponse().getOutputStream());
            writer = new PrintWriter(new OutputStreamWriter(copiedOutput, getCharacterEncoding()), true);
        }

        return writer;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException("getWriter() has already been called on this response.");
        }

        if (outputStream == null) {
            outputStream = getResponse().getOutputStream();
            copiedOutput = new CachedServletOutputStream(outputStream);
        }

        return copiedOutput;
    }
    
//    public String getContent2() {
//    	return new String(copiedOutput.getCopy());
//    }

    private static class CachedServletOutputStream extends ServletOutputStream {

        private OutputStream os;
        private ByteArrayOutputStream copy = new ByteArrayOutputStream(1024);

        public CachedServletOutputStream(OutputStream outputStream) {
            this.os = outputStream;
        }

        @Override
        public void write(int b) throws IOException {
            os.write(b);
            copy.write(b);
        }

        public byte[] getCopy() {
            return copy.toByteArray();
        }

		@Override
		public boolean isReady() {
			return true;
		}

		@Override
		public void setWriteListener(WriteListener arg0) {
			throw new RuntimeException("Not implemented");
		}
    }
}

