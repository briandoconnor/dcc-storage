/*
 * Copyright (c) 2015 The Ontario Institute for Cancer Research. All rights reserved.                             
 *                                                                                                               
 * This program and the accompanying materials are made available under the terms of the GNU Public License v3.0.
 * You should have received a copy of the GNU General Public License along with                                  
 * this program. If not, see <http://www.gnu.org/licenses/>.                                                     
 *                                                                                                               
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY                           
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES                          
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT                           
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,                                
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED                          
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;                               
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER                              
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN                         
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package collaboratory.storage.object.store.client.download;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.annotation.PostConstruct;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import collaboratory.storage.object.store.client.upload.ObjectStoreServiceProxy;
import collaboratory.storage.object.store.client.upload.ProgressBar;
import collaboratory.storage.object.store.core.model.ObjectSpecification;
import collaboratory.storage.object.store.core.model.Part;
import collaboratory.storage.object.transport.ObjectTransport;

/**
 * main class to handle uploading objects
 */
@Slf4j
@Component
public class ObjectDownload {

  @Autowired
  private ObjectStoreServiceProxy proxy;

  @Autowired
  private ObjectTransport.Builder transportBuilder;

  @Value("${client.upload.retryNumber}")
  private int retryNumber;

  @PostConstruct
  public void setup() {
    retryNumber = retryNumber < 0 ? Integer.MAX_VALUE : retryNumber;
  }

  /**
   * the only public method for client to call to upload data to collaboratory
   * 
   * @param file The file to be uploaded
   * @param objectId The object id that is used to associate the file in the collaboratory
   * @param redo If redo the upload is required
   * @throws IOException
   */
  public void download(File outputDirectory, String objectId, boolean redo) throws IOException {
    startNewDownload(outputDirectory, objectId);
  }

  /**
   * Start a upload given the object id
   */
  @SneakyThrows
  private void startNewDownload(File dir, String objectId) {
    log.info("Start a new download...");
    Files.createDirectory(dir.toPath());
    ObjectSpecification spec = proxy.getDownloadSpecification(objectId);
    // TODO: assign session id
    downloadParts(spec.getParts(), dir, objectId, spec.getUploadId(), new ProgressBar(spec.getParts().size(), spec
        .getParts().size()));
  }

  /**
   * start upload parts using a specific configured data transport
   */
  @SneakyThrows
  private void downloadParts(List<Part> parts, File file, String objectId, String sessionId, ProgressBar progressBar) {
    transportBuilder.withProxy(proxy)
        .withProgressBar(progressBar)
        .withParts(parts)
        .withObjectId(objectId)
        .withSessionId(sessionId);
    log.debug("Transport Configuration: {}", transportBuilder);
    transportBuilder.build().receive(file);
  }

}