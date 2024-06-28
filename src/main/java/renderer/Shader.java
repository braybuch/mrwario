package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filePath;

    public Shader(String filepath){
        System.out.println("Hello OpenGL v" + glGetString(GL_VERSION));
        System.out.println("Hello GLSL v" + glGetString(GL_SHADING_LANGUAGE_VERSION));
        this.filePath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filePath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            // Find the second pattern after #type
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            // Assign the tokens
            if (firstPattern.equals("vertex")){
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")){
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token: " + firstPattern + " in " + filepath);
            }

            if (secondPattern.equals("vertex")){
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")){
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token: " + secondPattern + " in " + filepath);
            }

        } catch(IOException e){
            e.printStackTrace();
            assert false : "Error: Could not open file for shader";
        }
    }

    public void compileAndLink(){
        int vertexID, fragmentID;

        // Load and compile vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // Check for errors in compilation process
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: " + filePath + "\n\tVertex shader compilation failed");
            System.out.println(glGetShaderInfoLog(vertexID, length));
            assert false : "";
        }

        // Load and compile fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors in compilation process
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success == GL_FALSE) {
            int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: " + filePath + "\n\tFragment shader compilation failed");
            System.out.println(glGetShaderInfoLog(fragmentID, length));
            assert false : "";
        }

        // Link shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success == GL_FALSE) {
            int length = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("Error: " + filePath + "\n\tLinking shaders failed");
            System.out.println(glGetProgramInfoLog(shaderProgramID, length));
            assert false : "";
        }
    }

    public void use(){
        if (!beingUsed) {
            // Bind shader program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach(){
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMatrix4(String varName, Matrix4f mat4){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMatrix3(String varName, Matrix3f mat3f){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3f.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadVector4(String varName, Vector4f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVector3(String varName, Vector3f vec){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVector2(String varName, Vector2f vec){
        int location = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(location, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float f){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, f);
    }

    public void uploadInt(String varName, int i){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, i);
    }

    public void uploadTexture(String varName, int slot){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }

    public void uploadIntArray(String varName, int[] array){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, array);
    }
}
