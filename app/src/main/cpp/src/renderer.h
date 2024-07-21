//
// Created by PlaneJun on 2024/7/18.
//

#ifndef ANDROID_IMGUI_DEMO_RENDERER_H
#define ANDROID_IMGUI_DEMO_RENDERER_H

namespace Renderer
{
    static ImGuiWindow* g_imguiWin = nullptr;

    void SetupRender()
    {
        if(g_Initialized)
        {
            return;
        }

        // Setup Dear ImGui context
        IMGUI_CHECKVERSION();
        ImGui::CreateContext();
        ImGuiIO& io = ImGui::GetIO();

        // Setup Dear ImGui style
        ImGui::StyleColorsDark();
        //ImGui::StyleColorsLight();

        // Setup Platform/Renderer backends
        ImGui_ImplAndroid_Init(nullptr);
        ImGui_ImplOpenGL3_Init("#version 300 es");

        ImFontConfig font_cfg;
        font_cfg.SizePixels = 22.0f;
        io.Fonts->AddFontDefault(&font_cfg);

        // Arbitrary scale-up
        // FIXME: Put some effort into DPI awareness
        ImGui::GetStyle().ScaleAllSizes(3.0f);

        g_Initialized = true;
    }

    void Render()
    {
        ImGuiIO& io = ImGui::GetIO();

        // Our state
        // (we use static, which essentially makes the variable globals, as a convenience to keep the example code easy to follow)
        static bool show_demo_window = true;
        static ImVec4 clear_color = ImVec4(0.45f, 0.55f, 0.60f, 0.00f);

        // Start the Dear ImGui frame
        ImGui_ImplOpenGL3_NewFrame();
        ImGui_ImplAndroid_NewFrame(g_ScreenWidth,g_ScreenHeight);
        ImGui::NewFrame();

        // 1. Show the big demo window (Most of the sample code is in ImGui::ShowDemoWindow()! You can browse its code to learn more about Dear ImGui!).
        {
            static float f = 0.0f;
            static int counter = 0;

            ImGui::Begin("Hello, world!"); // Create a window called "Hello, world!" and append into it.

            g_imguiWin = ImGui::GetCurrentWindow();

            ImGui::Text("This is some useful text.");               // Display some text (you can use a format strings too)
            ImGui::Checkbox("Demo Window", &show_demo_window);      // Edit bools storing our window open/close state

            ImGui::SliderFloat("float", &f, 0.0f, 1.0f);            // Edit 1 float using a slider from 0.0f to 1.0f
            ImGui::ColorEdit3("clear color", (float*)&clear_color); // Edit 3 floats representing a color

            if (ImGui::Button("Button"))                            // Buttons return true when clicked (most widgets return true when edited/activated)
                counter++;
            ImGui::SameLine();
            ImGui::Text("counter = %d", counter);

            ImGui::Text("Application average %.3f ms/frame (%.1f FPS)", 1000.0f / io.Framerate, io.Framerate);
            ImGui::End();
        }

        ImGui::GetForegroundDrawList()->AddLine({100,100},{200,200},IM_COL32_WHITE,1);

        // Rendering
        ImGui::Render();
        glClear(GL_COLOR_BUFFER_BIT);
        ImGui_ImplOpenGL3_RenderDrawData(ImGui::GetDrawData());
    }

    void Resize(int width, int height)
    {
        g_ScreenWidth = width;
        g_ScreenHeight = height;
        glViewport(0, 0, width, height);
        ImGuiIO &io = ImGui::GetIO();
        ImGui::GetIO().DisplaySize = ImVec2((float)width, (float)height);
    }

    ImGuiWindow* GetWindow() {
        return g_imguiWin;
    }
}

#endif //ANDROID_IMGUI_DEMO_RENDERER_H
