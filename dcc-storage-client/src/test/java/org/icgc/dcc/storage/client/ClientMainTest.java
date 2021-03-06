/*
 * Copyright (c) 2016 The Ontario Institute for Cancer Research. All rights reserved.                             
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
package org.icgc.dcc.storage.client;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import lombok.val;

public class ClientMainTest extends AbstractClientMainTest {

  @Rule
  public TemporaryFolder tmp = new TemporaryFolder();

  @Test
  public void testMainViewFileWithBadOutputType() throws Exception {
    executeMain("view", "--output-type", "xxx");

    assertTrue(getExitCode() == 1);
    assertTrue(
        getOutput().contains("Bad parameter(s): \"--output-type\": couldn't convert \"xxx\" to a value in [bam, sam]"));
  }

  @Test
  public void testMainViewFileWithUpperCaseOutputType() throws Exception {
    executeMain("view", "--output-type", "BAM");

    assertTrue(getExitCode() == 1);
    assertTrue(getOutput().contains("One of --object-id or --input-file must be specified"));
  }

  @Test
  public void testMainUploadEmptyFile() throws Exception {
    val file = tmp.newFile();
    executeMain("upload", "--object-id", UUID.randomUUID().toString(), "--file", file.getCanonicalPath());

    assertTrue(getExitCode() == 1);
    assertTrue(getOutput().contains("Uploads of empty files are not permitted"));
  }

  @Test
  public void testMainDownloadWithNonExistentManifest() throws Exception {
    val file = new File("/foo");
    val outDir = tmp.newFolder();
    executeMain("download", "--manifest", file.getCanonicalPath(), "--output-dir", outDir.getCanonicalPath());

    assertTrue(getExitCode() == 1);
    assertTrue(getOutput().contains("Could not read manifest from 'file:/foo': /foo (No such file or directory)"));
  }

  @Test
  public void testMainDownloadWithNonExistentOutDir() throws Exception {
    val file = tmp.newFile();
    val outDir = new File("/foo");
    executeMain("download", "--manifest", file.getCanonicalPath(), "--output-dir", outDir.getCanonicalPath());

    assertTrue(getExitCode() == 1);
    assertTrue(getOutput().contains("Bad parameter(s): Invalid option: --output-dir: /foo does not exist"));
  }

  @Test
  public void testMainDownloadWithEmptyManifest() throws Exception {
    val file = tmp.newFile();
    val outDir = tmp.newFolder();
    executeMain("download", "--manifest", file.getCanonicalPath(), "--output-dir", outDir.getCanonicalPath());

    assertTrue(getExitCode() == 1);
    assertTrue(getOutput().contains(" is empty"));
  }

  @Ignore
  @Test
  public void testMainDownloadWithPopulatedManifest() throws Exception {
    val file = new File("src/test/resources/fixtures/download/manifest.txt");
    val outDir = tmp.newFolder();
    executeMain("download", "--manifest", file.getCanonicalPath(), "--output-dir", outDir.getCanonicalPath());

    assertTrue(getExitCode() == 1);
    assertTrue(getOutput().contains(" is empty. Exiting."));
  }

  @Test
  public void testMainDownloadWithBadObjectId() throws Exception {
    val outDir = tmp.newFolder();
    executeMain("download", "--object-id", "xxx", "--out-dir", outDir.getCanonicalPath());

    assertTrue(getExitCode() == 1);
    assertTrue(getOutput().contains("Invalid option: --object-id: xxx is not a valid UUID"));
  }

  @Test
  public void testViewWithBadDateInHeader() throws Exception {
    val file = "src/test/resources/fixtures/view/94c1f438-acc8-51dd-a44e-e24d32a46c07.bam";
    executeMain("view", "--header-only", "--input-file", file, "--output-type", "sam");

    assertTrue(getExitCode() == 0);
  }

}
