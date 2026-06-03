#include <QApplication>
#include <QFile>
#include <QDir>
#include "GacoanApp.h"

int main(int argc, char *argv[]) {
    QApplication app(argc, argv);

    QFile styleFile(QDir(QCoreApplication::applicationDirPath()).filePath("style.qss"));
    if (!styleFile.exists()) {
        styleFile.setFileName("style.qss");
    }
    if (styleFile.open(QFile::ReadOnly | QFile::Text)) {
        QString style = styleFile.readAll();
        app.setStyleSheet(style);
        styleFile.close();
    }

    GacoanApp window;
    window.show();

    return app.exec();
}
