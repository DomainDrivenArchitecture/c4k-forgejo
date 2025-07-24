from os import environ
from subprocess import run
from pybuilder.core import init, task
from ddadevops import *

default_task = "dev"
name = 'c4k-forgejo'
MODULE = 'not-used'
PROJECT_ROOT_PATH = '.'

@init
def initialize(project):
    project.build_depends_on("ddadevops>=4.7.0")

    input = {
        "name": name,
        "module": MODULE,
        "stage": "notused",
        "project_root_path": PROJECT_ROOT_PATH,
        "build_types": [],
        "mixin_types": ["RELEASE"],
        "release_primary_build_file": "project.clj",
        "release_secondary_build_files": [
            "package.json",
            "infrastructure/backup/build.py",
            ],
        "release_artifact_server_url": "https://repo.prod.meissa.de",
        "release_organisation": "meissa",
        "release_repository_name": name,
        "release_artifacts": [
            "target/graalvm/" + name,
            "target/uberjar/" + name + "-standalone.jar",
        ],
    }
    
    build = ReleaseMixin(project, input)
    build.initialize_build_dir()


@task
def test_clj(project):
    run("lein test", shell=True, check=True)


@task
def test_schema(project):
    run("lein uberjar", shell=True, check=True)
    run(
        "java -jar target/uberjar/c4k-forgejo-standalone.jar "
        + "src/test/resources/forgejo-test/valid-config.yaml "
        + "src/test/resources/forgejo-test/valid-auth.yaml | "
        + """kubeconform --kubernetes-version 1.23.0 --strict --skip "Certificate,Middleware" -""",
        shell=True,
        check=True,
    )


@task
def package_uberjar(project):
    run(
        "sha256sum target/uberjar/c4k-forgejo-standalone.jar > target/uberjar/" + project.name + "-standalone.jar.sha256",
        shell=True,
        check=True,
    )
    run(
        "sha512sum target/uberjar/c4k-forgejo-standalone.jar > target/uberjar/" + project.name + "-standalone.jar.sha512",
        shell=True,
        check=True,
    )


@task
def package_native(project):
    run(
        "mkdir -p target/graalvm",
        shell=True,
        check=True,
    )
    run(
        "native-image " +
        "--native-image-info " +
        "--report-unsupported-elements-at-runtime " +
        "--no-server " +
        "--no-fallback " +
        "--features=clj_easy.graal_build_time.InitClojureClasses " +
        "-jar target/uberjar/" + project.name + "-standalone.jar " +
        "-march=compatibility " +
        "-H:IncludeResources=.*.yaml " +
        "-H:IncludeResources=logback.xml " +
        "-H:Log=registerResource:verbose " +
        "-H:Name=target/graalvm/" + project.name + "",
        shell=True,
        check=True,
    )
    run(
        "sha256sum target/graalvm/c4k-forgejo > target/graalvm/" + project.name + ".sha256",
        shell=True,
        check=True,
    )
    run(
        "sha512sum target/graalvm/c4k-forgejo > target/graalvm/" + project.name + ".sha512",
        shell=True,
        check=True,
    )


@task
def inst(project):
    run(
        "lein uberjar",
        shell=True,
        check=True,
    )
    run(
        "sudo install -m=755 target/uberjar/" + project.name + "-standalone.jar /usr/local/bin/" + project.name + "-standalone.jar",
        shell=True,
        check=True,
    )
    package_native(project)
    run(
        "sudo install -m=755 target/graalvm/" + project.name + " /usr/local/bin/" + project.name + "",
        shell=True,
        check=True,
    )


@task
def upload_clj(project):
    run("lein deploy", shell=True, check=True)


@task
def lint(project):
    run(
       "lein eastwood",
       shell=True,
       check=True,
    )
    run(
        "lein ancient check",
        shell=True,
        check=True,
    )


@task
def patch(project):
    linttest(project, "PATCH")
    release(project)


@task
def minor(project):
    linttest(project, "MINOR")
    release(project)


@task
def major(project):
    linttest(project, "MAJOR")
    release(project)


@task
def dev(project):
    linttest(project, "NONE")


@task
def prepare(project):
    build = get_devops_build(project)
    build.prepare_release()


@task
def tag(project):
    build = get_devops_build(project)
    build.tag_bump_and_push_release()

@task
def publish_artifacts(project):
    build = get_devops_build(project)
    build.publish_artifacts()

def release(project):
    prepare(project)
    tag(project)


def linttest(project, release_type):
    build = get_devops_build(project)
    build.update_release_type(release_type)
    test_clj(project)
    test_schema(project)
    lint(project)
