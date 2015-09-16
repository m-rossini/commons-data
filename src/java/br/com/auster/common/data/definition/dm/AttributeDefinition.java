/*
 * Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Created on 01/03/2006
 */

package br.com.auster.common.data.definition.dm;

import groovy.lang.Script;
import br.com.auster.common.asm.FieldDefinition;

public class AttributeDefinition extends FieldDefinition {
    private String          scriptText = null;
    private Script          script     = null;

    public AttributeDefinition(String name,
                               String scriptText,
                               Script script,
                               String type) {
        super(name, type);
        this.scriptText = scriptText;
        this.script = script;
    }

    /**
     * @return Returns the script.
     */
    public Script getScript() {
        return script;
    }

    /**
     * @param script The script to set.
     */
    public void setScript(Script script) {
        this.script = script;
    }

    /**
     * @return Returns the scriptText.
     */
    public String getScriptText() {
        return scriptText;
    }

    /**
     * @param scriptText The scriptText to set.
     */
    public void setScriptText(String scriptText) {
        this.scriptText = scriptText;
    }

    /**
     * @inheritDoc
     */
    public boolean equals(Object o) {
        return this.getName().equals( ((AttributeDefinition) o).getName() );
    }

    /**
     * @inheritDoc
     */
    public int hashCode() {
        return this.getName().hashCode();
    }
}