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
package org.icgc.dcc.storage.fs;

import static java.util.regex.Pattern.compile;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StorageFileAttributes implements PosixFileAttributes {

  private final Pattern REGULAR_FILE_PATTERN = compile(
      "/" + "[^/]*/" + ".+"); // <root>/ <dir>/ <file>

  /**
   * Configuration.
   */
  @NonNull
  private final StoragePath path;

  /**
   * Metadata.
   */
  @NonNull
  private final StorageContext context;

  @Override
  public FileTime lastModifiedTime() {
    return FileTime.fromMillis(0);
  }

  @Override
  public FileTime lastAccessTime() {
    return FileTime.fromMillis(0);
  }

  @Override
  public FileTime creationTime() {
    return FileTime.fromMillis(0);
  }

  @Override
  public boolean isRegularFile() {
    return matches(path, REGULAR_FILE_PATTERN);
  }

  @Override
  public boolean isDirectory() {
    return !isRegularFile();
  }

  @Override
  public boolean isSymbolicLink() {
    return path.endsWith(".json");
  }

  @Override
  public boolean isOther() {
    return false;
  }

  @Override
  public long size() {
    if (path.endsWith(".bam")) {
      return 200627495654L;
    } else if (path.endsWith(".bai")) {
      return 8264320L;
    } else if (path.endsWith(".json")) {
      return 1649;
    } else {
      return 258;
    }
  }

  @Override
  public Object fileKey() {
    return path.toAbsolutePath().toString();
  }

  @Override
  public UserPrincipal owner() {
    return new UserPrincipal() {

      @Override
      public String getName() {
        return "icgc-user";
      }

    };
  }

  @Override
  public GroupPrincipal group() {
    return new GroupPrincipal() {

      @Override
      public String getName() {
        return "icgc-group";
      }

    };
  }

  @Override
  public Set<PosixFilePermission> permissions() {
    return ImmutableSet.of(PosixFilePermission.OWNER_READ);
  }

  private static boolean matches(Path path, Pattern patern) {
    return patern.matcher(path.toString()).matches();
  }

}