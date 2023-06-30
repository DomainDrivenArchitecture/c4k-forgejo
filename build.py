from os import environ
from subprocess import run
from pybuilder.core import task, init
from ddadevops import *

name = 'c4k'
MODULE = 'forgejo'
PROJECT_ROOT_PATH = '..'

@init
def initialize(project):
    project.build_depends_on("ddadevops>=4.0.0")

    input = {
        "name": name,
        "module": MODULE,
        "stage": "notused",
        "project_root_path": PROJECT_ROOT_PATH,
        "build_types": [],
        "mixin_types": ["RELEASE"],
        "release_primary_build_file": "project.clj",
        "release_secondary_build_files": ["package.json"],
    }
    
    build = ReleaseMixin(project, input)
    build.initialize_build_dir()


@task
def prepare_release(project):
    build = get_devops_build(project)
    build.prepare_release()

@task
def tag_bump_and_push_release(project):
    build = get_devops_build(project)
    build.tag_bump_and_push_release()

@task
def patch(project):
    build_all(project, "PATCH")

@task
def minor(project):
    build_all(project, "MINOR")

@task
def major(project):
    build_all(project, "MAJOR")
    
@task
def dev(project):
    build_all(project, "NONE")

@task
def test(project):
    run("lein test", shell=True)

@task
def build_it(project):
    run("lein uberjar", shell=True)

@task
def publish(project):
    run("lein deploy", shell=True)

def build_all(project, release_type):
    build = get_devops_build(project)
    build.update_release_type(release_type)
    test(project)
    prepare_release(project)
    build_it(project)
    tag_bump_and_push_release(project)

