//
// Created by PlaneJun on 2024/7/18.
//

#ifndef ANDROID_IMGUI_DEMO_JAVA_H
#define ANDROID_IMGUI_DEMO_JAVA_H

extern "C"
{
    JNIEXPORT void JNICALL
    Java_com_example_android_1imgui_1demo_NativeMethods_onDrawFrame(JNIEnv *env, jclass clazz) {
        Renderer::Render();
    }

    JNIEXPORT void JNICALL
    Java_com_example_android_1imgui_1demo_NativeMethods_onSurfaceCreated(JNIEnv *env, jclass clazz) {
        Renderer::SetupRender();
    }

    JNIEXPORT void JNICALL
    Java_com_example_android_1imgui_1demo_NativeMethods_onSurfaceChanged(JNIEnv *env, jclass clazz, jint width,jint height) {
        Renderer::Resize(width,height);
    }

    JNIEXPORT void JNICALL
    Java_com_example_android_1imgui_1demo_NativeMethods_onDetachedFromWindow(JNIEnv *env, jclass clazz) {
        if (!g_Initialized)
        {
            return;
        }
        // Cleanup
        ImGui_ImplOpenGL3_Shutdown();
        ImGui_ImplAndroid_Shutdown();
        ImGui::DestroyContext();
        g_Initialized=false;
    }

    JNIEXPORT void JNICALL
    Java_com_example_android_1imgui_1demo_NativeMethods_MotionEventClick(JNIEnv *env, jclass clazz,
                                                                  jboolean down, jfloat pos_x,
                                                                  jfloat pos_y) {
        ImGuiIO & io = ImGui::GetIO();
        io.MouseDown[0] = down;
        io.MousePos = ImVec2(pos_x,pos_y);
    }

    JNIEXPORT jstring JNICALL
    Java_com_example_android_1imgui_1demo_NativeMethods_GetWindowRect(JNIEnv *env, jclass clazz) {
        char result[256]="0|0|0|0";

        ImGuiWindow* win = Renderer::GetWindow();
        if(win)
        {
            sprintf(result,"%d|%d|%d|%d",(int)win->Pos.x,(int)win->Pos.y,(int)win->Size.x,(int)win->Size.y);
        }
        return env->NewStringUTF(result);
    }

    JNIEXPORT void JNICALL
    Java_com_example_android_1imgui_1demo_NativeMethods_SetWindowSize(JNIEnv *env, jclass clazz, jint w,
                                                             jint h) {
        g_ScreenWidth = w;
        g_ScreenHeight = h;
    }
}

#endif //ANDROID_IMGUI_DEMO_JAVA_H
