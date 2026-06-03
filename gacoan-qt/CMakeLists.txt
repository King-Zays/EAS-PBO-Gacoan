cmake_minimum_required(VERSION 3.16)
project(GacoanQt LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_AUTOMOC ON)
set(CMAKE_EXPORT_COMPILE_COMMANDS ON)

# ---- Find Qt ----
# Try Qt6 first, fallback to Qt5
find_package(Qt6 COMPONENTS Widgets QUIET)
if(Qt6_FOUND)
    message(STATUS "Using Qt6: ${Qt6Widgets_VERSION}")
    set(QT_LIBS Qt6::Widgets)
else()
    find_package(Qt5 COMPONENTS Widgets REQUIRED)
    message(STATUS "Using Qt5: ${Qt5Widgets_VERSION}")
    set(QT_LIBS Qt5::Widgets)
endif()

# ---- Sources ----
set(SOURCES
    main.cpp
    GacoanApp.cpp
    GacoanEngine.cpp
)

set(HEADERS
    GacoanApp.h
    GacoanEngine.h
    Menu.h
    ItemPesanan.h
    TransaksiPesanan.h
)

# ---- Build Executable ----
add_executable(${PROJECT_NAME} ${SOURCES} ${HEADERS})
target_link_libraries(${PROJECT_NAME} PRIVATE ${QT_LIBS})

# ---- Windows-specific: Link SAPI/COM libraries for TTS ----
if(WIN32)
    target_link_libraries(${PROJECT_NAME} PRIVATE ole32 oleaut32 uuid winmm)
endif()

# ---- Copy QSS to build directory ----
configure_file(
    ${CMAKE_CURRENT_SOURCE_DIR}/style.qss
    ${CMAKE_CURRENT_BINARY_DIR}/style.qss
    COPYONLY
)

# ---- Copy Audio folder to build directory ----
file(COPY ${CMAKE_CURRENT_SOURCE_DIR}/../audio DESTINATION ${CMAKE_CURRENT_BINARY_DIR})
