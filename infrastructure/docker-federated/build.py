from os import environ
from pybuilder.core import task, init
from ddadevops import *

name = 'c4k-forgejo-federated'
MODULE = 'docker'
PROJECT_ROOT_PATH = '../..'

@init
def initialize(project):
    input = {
        "name": name,
        "module": MODULE,
        "stage": "notused",
        "project_root_path": PROJECT_ROOT_PATH,
        "build_types": ["IMAGE"],
        "mixin_types": [],
    }

    project.build_depends_on('ddadevops>=4.0.0-dev')

    build = DevopsImageBuild(project, input)
    build.initialize_build_dir()


@task
def image(project):
    build = get_devops_build(project)
    build.image()

@task
def drun(project):
    build = get_devops_build(project)
    build.drun()

@task
def publish(project):
    build = get_devops_build(project)
    build.dockerhub_login()
    build.dockerhub_publish()

@task
def test(project):
    build = get_devops_build(project)
    build.test()
