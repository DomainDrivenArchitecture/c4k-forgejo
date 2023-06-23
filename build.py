from os import environ
from pybuilder.core import task, init
from ddadevops import *

name = 'forgejo'
MODULE = 'c4k'
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
def after_publish(project):
    build = get_devops_build(project)
    build.tag_bump_and_push_release()