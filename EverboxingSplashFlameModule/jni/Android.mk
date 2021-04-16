LOCAL_PATH := $(call my-dir)

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
include $(BUILD_SHARED_LIBRARY)

