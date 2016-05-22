LOCAL_PATH := $(call my-dir)

# include $(CLEAR_VARS)
# LOCAL_MODULE := tiff_utils
# LOCAL_SRC_FILES := /usr/local/libtiff-4.0.6/lib/libtiff.a
# LOCAL_SRC_FILES := inc/.libs/libtiff.a
# LOCAL_EXPORT_C_INCLUDES := /usr/local/libtiff-4.0.6/include
# LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/inc
# include $(PREBUILT_STATIC_LIBRARY)
# INC := $(wildcard $(LOCAL_PATH)/inc/*.c)

# ----
# JPEG
# ----
include $(CLEAR_VARS)
INC_LJPEG := $(wildcard $(LOCAL_PATH)/ljpeg/*.h)
SRC_FRA := $(wildcard $(LOCAL_PATH)/ljpeg/*.c)

LOCAL_ARM_MODE := arm
LOCAL_C_INCLUDES := $(INC_LJPEG:$(LOCAL_PATH)/%=%)
LOCAL_STATIC_LIBRARIES := \
                    $(LOCAL_PATH)/libs/libjpeg.a \
                    $(LOCAL_PATH)/libs/libjpeg-x86.a

LOCAL_CFLAGS += -DAVOID_TABLES
LOCAL_CFLAGS += -O3 -fstrict-aliasing -fprefetch-loop-arrays

LOCAL_SRC_FILES := $(SRC_FRA:$(LOCAL_PATH)/%=%)

LOCAL_LDLIBS    := -llog -lz
    # -L $(LOCAL_PATH)/libs \
    # -L $(LOCAL_STATIC_LIBRARIES) \
    # -ljpeg

ifeq ($(TARGET_ARCH),arm)
	LOCAL_LDLIBS += $(LOCAL_PATH)/libs/libjpeg.a
else
	ifeq ($(TARGET_ARCH),x86)
		LOCAL_LDLIBS += $(LOCAL_PATH)/libs/libjpeg-x86.a
	else
		LOCAL_LDLIBS += $(LOCAL_PATH)/libs/libjpeg-mips.a
	endif
endif

LOCAL_MODULE    := splash_flame
# LOCAL_MODULE    := ljpeg
# LOCAL_SRC_FILES := $(SRC_FRA:$(LOCAL_PATH)/%=%)

# LOCAL_SRC_FILES := fractal.c
# LOCAL_SRC_FILES := $(INC:$(LOCAL_PATH)/%=%)
# LOCAL_SRC_FILES := $(INC_LJPEG:$(LOCAL_PATH)/%=%)
# LOCAL_STATIC_LIBRARIES := tiff_utils
# LOCAL_LDLIBS    := -llog -lz
include $(BUILD_SHARED_LIBRARY)

# -----
# FLAME
# -----
# include $(CLEAR_VARS)
# SRC_FRA := $(wildcard $(LOCAL_PATH)/src/*.c)
# LOCAL_MODULE := splash_flame
# LOCAL_CFLAGS := -DANDROID_NDK
# LOCAL_SRC_FILES := $(SRC_FRA:$(LOCAL_PATH)/%=%)
# LOCAL_LDLIBS := -llog
# LOCAL_SHARED_LIBRARIES := jpeg
# include $(BUILD_SHARED_LIBRARY)
