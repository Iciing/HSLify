package org.polyfrost.example;

import cc.polyfrost.oneconfig.events.event.InitializationEvent;
import cc.polyfrost.oneconfig.utils.commands.CommandManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.*;
import org.polyfrost.example.command.ExampleCommand;
import org.polyfrost.example.config.TestConfig;

import java.io.*;
import java.io.IOException;

/**
 * The entrypoint of the Example Mod that initializes it.
 *
 * @see Mod
 * @see InitializationEvent
 */
@Mod(modid = ExampleMod.MODID, name = ExampleMod.NAME, version = ExampleMod.VERSION)
public class ExampleMod {
    public static final String MODID = "0001";
    public static final String NAME = "HSLify";
    public static final String VERSION = "1.0";
    // Sets the variables from `gradle.properties`. See the `blossom` config in `build.gradle.kts`.
    @Mod.Instance(MODID)
    public static ExampleMod INSTANCE; // Adds the instance of the mod, so we can access other variables.
    public static TestConfig config;
    private ShaderLinkHelper ShaderHelper;

    // Register the config and commands.
    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        config = new TestConfig();
        CommandManager.INSTANCE.registerCommand(new ExampleCommand());
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT && TestConfig.enableFilters) {
            applyFilters();
        }
    }

    private int hslShader;

    private void applyFilters() {
        float hue = TestConfig.hueValue;
        float saturation = TestConfig.saturationValue;
        float lightness = TestConfig.lightnessValue;

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, Minecraft.getMinecraft().getFramebuffer().framebufferTexture);

        ARBShaderObjects.glUseProgramObjectARB(hslShader);

        int hueLocation = ARBShaderObjects.glGetUniformLocationARB(hslShader, "hue");
        int saturationLocation = ARBShaderObjects.glGetUniformLocationARB(hslShader, "saturation");
        int lightnessLocation = ARBShaderObjects.glGetUniformLocationARB(hslShader, "lightness");

        ARBShaderObjects.glUniform1fARB(hueLocation, hue);
        ARBShaderObjects.glUniform1fARB(saturationLocation, saturation);
        ARBShaderObjects.glUniform1fARB(lightnessLocation, lightness);

        renderFullScreenQuad();

        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    private void loadShader() throws IOException {
        class ShaderHelper {

            public int createProgram(String vertexShaderPath, String fragmentShaderPath) throws IOException {
                int programID = ARBShaderObjects.glCreateProgramObjectARB();
                if (programID == 0) {
                    throw new RuntimeException("Error creating shader program.");
                }

                int vertexShader = createShader(vertexShaderPath, ARBVertexShader.GL_VERTEX_SHADER_ARB);
                int fragmentShader = createShader(fragmentShaderPath, ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);

                ARBShaderObjects.glAttachObjectARB(programID, vertexShader);
                ARBShaderObjects.glAttachObjectARB(programID, fragmentShader);

                ARBShaderObjects.glLinkProgramARB(programID);

                if (ARBShaderObjects.glGetObjectParameteriARB(programID, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
                    throw new RuntimeException("Error linking shader program: " + getLogInfo(programID));
                }

                ARBShaderObjects.glValidateProgramARB(programID);

                if (ARBShaderObjects.glGetObjectParameteriARB(programID, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
                    throw new RuntimeException("Error validating shader program: " + getLogInfo(programID));
                }

                return programID;
            }

            private int createShader(String filename, int shaderType) throws IOException {
                int shader = 0;
                try {
                    shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

                    if (shader == 0) {
                        return 0;
                    }

                    ARBShaderObjects.glShaderSourceARB(shader, readShaderFileAsString(filename));
                    ARBShaderObjects.glCompileShaderARB(shader);

                    if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
                        throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
                    }

                    return shader;
                } catch (Exception e) {
                    ARBShaderObjects.glDeleteObjectARB(shader);
                    throw e;
                }
            }

            private String readShaderFileAsString(String hsl_filter) throws IOException {
                StringBuilder shaderSource = new StringBuilder();
                InputStream inputStream = ShaderHelper.class.getResourceAsStream(hsl_filter);

                if (inputStream == null) {
                    throw new IOException("Shader file not found: " + hsl_filter);
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        shaderSource.append(line).append('\n');
                    }
                }

                return shaderSource.toString();
            }

            private String getLogInfo(int obj) {
                int logLength = ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB);

                if (logLength > 0) {
                    return ARBShaderObjects.glGetInfoLogARB(obj, logLength);
                } else {
                    return "No log available.";
                }
            }
        }
    }

    private void renderFullScreenQuad() {
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(0, 0);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(0, 1);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(1, 1);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(1, 0);
        GL11.glEnd();
    }
}
